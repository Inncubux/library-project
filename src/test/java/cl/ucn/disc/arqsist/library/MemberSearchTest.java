/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library;

import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.db.Database;
import cl.ucn.disc.arqsist.library.grpc.MemberClient;
import cl.ucn.disc.arqsist.library.grpc.MemberGrpcService;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.service.MemberService;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the SearchMembers gRPC RPC.
 */
final class MemberSearchTest {

    @Test
    void searchMembersAcceptanceCases() throws Exception {
        Database db = new Database("jdbc:sqlite::memory:");
        MemberService memberService = new MemberService(new MemberDao(db.connectionSource()));

        String serverName = InProcessServerBuilder.generateName();
        Server server = InProcessServerBuilder.forName(serverName).directExecutor()
                .addService(new MemberGrpcService(memberService))
                .build()
                .start();
        ManagedChannel channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();

        try (MemberClient client = new MemberClient(channel)) {
            client.register(new Member("Ada Lovelace", "ada@example.com"));
            client.register(new Member("Grace Hopper", "grace@example.com"));
            client.register(new Member("Alan Turing", "alan@example.com"));

            // Case 1: "ada" -> Ada Lovelace
            List<Member> res1 = client.search("ada");
            assertEquals(1, res1.size());
            assertEquals("Ada Lovelace", res1.get(0).getName());

            // Case 2: "ADA" -> Ada Lovelace (case-insensitive)
            List<Member> res2 = client.search("ADA");
            assertEquals(1, res2.size());
            assertEquals("Ada Lovelace", res2.get(0).getName());

            // Case 3: "  ada  " -> Ada Lovelace (trimmed)
            List<Member> res3 = client.search("  ada  ");
            assertEquals(1, res3.size());
            assertEquals("Ada Lovelace", res3.get(0).getName());

            // Case 4: "a" -> Ada Lovelace, Grace Hopper, Alan Turing (ordered by ID asc)
            List<Member> res4 = client.search("a");
            assertEquals(3, res4.size());
            assertEquals("Ada Lovelace", res4.get(0).getName());
            assertEquals("Grace Hopper", res4.get(1).getName());
            assertEquals("Alan Turing", res4.get(2).getName());

            // Case 5: "zzz" -> empty list, status OK
            List<Member> res5 = client.search("zzz");
            assertTrue(res5.isEmpty());

            // Case 6: "%" and "_" -> empty list (literal match, not SQL wildcards)
            assertTrue(client.search("%").isEmpty());
            assertTrue(client.search("_").isEmpty());

            // Case 7: "" and "   " -> IllegalArgumentException (from INVALID_ARGUMENT)
            assertThrows(IllegalArgumentException.class, () -> client.search(""));
            assertThrows(IllegalArgumentException.class, () -> client.search("   "));
        } finally {
            server.shutdownNow();
        }
    }
}