package com.sequenceiq.environment.api.v1.environment.model.request.aws;

import java.io.Serializable;

import com.sequenceiq.environment.api.doc.environment.EnvironmentModelDescription;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "UpdateAwsEncryptionParametersV1Request")
public class UpdateAwsDiskEncryptionParametersRequest implements Serializable {

    @ApiModelProperty(EnvironmentModelDescription.RESOURCE_ENCRYPTION_PARAMETERS)
    private AwsDiskEncryptionParameters awsDiskEncryptionParameters;

    public AwsDiskEncryptionParameters getAwsDiskEncryptionParameters() {
        return awsDiskEncryptionParameters;
    }

    public void setAwsDiskEncryptionParameters(AwsDiskEncryptionParameters awsDiskEncryptionParameters) {
        this.awsDiskEncryptionParameters = awsDiskEncryptionParameters;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "UpdateAwsEncryptionParametersRequest{" +
                "AwsDiskEncryptionParameters='" + awsDiskEncryptionParameters + '\'' +
                '}';
    }

    public static class Builder {
        private AwsDiskEncryptionParameters awsDiskEncryptionParameters;

        private Builder() {
        }

        public Builder withAwsDiskEncryptionParameters(AwsDiskEncryptionParameters awsDiskEncryptionParameters) {
            this.awsDiskEncryptionParameters = awsDiskEncryptionParameters;
            return this;
        }

        public UpdateAwsDiskEncryptionParametersRequest build() {
            UpdateAwsDiskEncryptionParametersRequest updateAwsEncryptionParametersRequest = new UpdateAwsDiskEncryptionParametersRequest();
            updateAwsEncryptionParametersRequest.setAwsDiskEncryptionParameters(awsDiskEncryptionParameters);
            return updateAwsEncryptionParametersRequest;
        }
    }
}
