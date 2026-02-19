package com.nationconnect.state_civil_service.grpc;

import com.nationconnect.identity.grpc.IdentityRequest;
import com.nationconnect.identity.grpc.IdentityResponse;
import com.nationconnect.identity.grpc.IdentityVerificationServiceGrpc;

import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;

import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class IdentityClient {

    @GrpcClient("identity-service")
    private IdentityVerificationServiceGrpc.IdentityVerificationServiceBlockingStub stub;

    @Retryable(
            retryFor = { StatusRuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public boolean verify(String nationalId) {
        // 🛡️ Protection contre le crash NullPointerException
        if (nationalId == null || nationalId.trim().isEmpty()) {
            System.out.println("⚠️ ALERTE : nationalId est NULL ou VIDE. Annulation de l'appel gRPC.");
            return false; 
        }

        try {
            IdentityRequest request = IdentityRequest.newBuilder()
                    .setNationalId(nationalId)
                    .build();

            // Appel au serveur gRPC (port 9090)
            IdentityResponse response = stub.verifyNationalId(request);
            
            // Log du résultat pour débugger la 400 Bad Request
            System.out.println("🔍 gRPC RESULT pour " + nationalId + " : " + response.getValid());
            
            return response.getValid();

        } catch (StatusRuntimeException e) {
            System.out.println("❌ gRPC CONNECTION ERROR: " + e.getStatus().getCode() + " | " + e.getMessage());
            throw e; // Relance pour activer le @Retryable
        }
    }

    @Recover
    public boolean recover(StatusRuntimeException e, String nationalId) {
        System.out.println("🛑 gRPC ECHEC FINAL APRES RETRIES : " + e.getStatus().getCode());
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Service d'identité injoignable après 3 tentatives."
        );
    }
}