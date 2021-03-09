package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointResult;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointStatus;

public class FetchReputationUseCaseSync {

    private GetReputationHttpEndpointSync getReputationHttpEndpointSync;

    public enum UseCaseResult{
        FAILURE,
        SUCCESS
    }

    public FetchReputationUseCaseSync(GetReputationHttpEndpointSync getReputationHttpEndpointSync) {
        this.getReputationHttpEndpointSync = getReputationHttpEndpointSync;
    }

    public ReputationResult getReputationSync() {
        EndpointResult result = getReputationHttpEndpointSync.getReputationSync();
        if(result.getStatus() == EndpointStatus.SUCCESS){
            return new ReputationResult(
                    UseCaseResult.SUCCESS,
                    result.getReputation()
            );
        }
        return new ReputationResult(
                UseCaseResult.FAILURE,
                0
        );
    }

    public class ReputationResult {

        private UseCaseResult status;
        private int reputation;

        public ReputationResult(UseCaseResult result, int reputation){
            this.status = result;
            this.reputation = reputation;
        }

        public UseCaseResult getStatus() {
            return status;
        }

        public void setStatus(UseCaseResult status) {
            this.status = status;
        }

        public int getReputation() {
            return reputation;
        }

        public void setReputation(int reputation) {
            this.reputation = reputation;
        }
    }

}
