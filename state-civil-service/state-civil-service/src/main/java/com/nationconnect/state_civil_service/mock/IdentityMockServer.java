package com.nationconnect.state_civil_service.mock;

import com.nationconnect.identity.grpc.IdentityRequest;
import com.nationconnect.identity.grpc.IdentityResponse;
import com.nationconnect.identity.grpc.IdentityVerificationServiceGrpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class IdentityMockServer {

    private Server server;

    // ✅ Démarre le serveur gRPC quand Spring est prêt
    @EventListener(ApplicationReadyEvent.class)
    public void startServer() throws Exception {

        server = ServerBuilder.forPort(9090)
                .addService(new IdentityServiceImpl())
                .build()
                .start();

        System.out.println("✅ SERVEUR MOCK gRPC DÉMARRÉ SUR LE PORT 9090");
    }

    // ✅ Implémentation du service défini dans identity.proto
    static class IdentityServiceImpl extends IdentityVerificationServiceGrpc.IdentityVerificationServiceImplBase {

        @Override
        public void verifyNationalId(IdentityRequest request,
                                     StreamObserver<IdentityResponse> responseObserver) {

            String id = request.getNationalId();

            // ✅ règle : valide si exactement 10 chiffres
            boolean isValid = (id != null && id.matches("\\d{10}"));

            IdentityResponse response = IdentityResponse.newBuilder()
                    .setValid(isValid)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
