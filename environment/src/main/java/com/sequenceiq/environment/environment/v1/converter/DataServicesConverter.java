package com.sequenceiq.environment.environment.v1.converter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.sequenceiq.environment.api.v1.environment.model.AwsDataServicesV1Parameters;
import com.sequenceiq.environment.api.v1.environment.model.AzureDataServicesV1Parameters;
import com.sequenceiq.environment.api.v1.environment.model.GcpDataServicesV1Parameters;
import com.sequenceiq.environment.api.v1.environment.model.request.DataServicesRequest;
import com.sequenceiq.environment.api.v1.environment.model.response.DataServicesResponse;
import com.sequenceiq.environment.environment.dto.dataservices.AwsDataServiceParameters;
import com.sequenceiq.environment.environment.dto.dataservices.AzureDataServiceParameters;
import com.sequenceiq.environment.environment.dto.dataservices.EnvironmentDataServices;
import com.sequenceiq.environment.environment.dto.dataservices.GcpDataServiceParameters;

@Component
public class DataServicesConverter {

    public EnvironmentDataServices convertToDto(DataServicesRequest dataServices) {
        if (dataServices == null) {
            return null;
        }
        EnvironmentDataServices dataServicesDto = EnvironmentDataServices.builder()
                .withAws(awsDataServicesToDto(dataServices.getAws()))
                .withAzure(azureDataServicesToDto(dataServices.getAzure()))
                .withGcp(gcpDataServicesToDto(dataServices.getGcp()))
                .build();
        return dataServicesDto;
    }

    public DataServicesResponse convertToResponse(EnvironmentDataServices dataServices) {
        if (dataServices == null) {
            return null;
        }
        DataServicesResponse response = new DataServicesResponse();
        response.setAws(awsDataServicesDtoToResponse(dataServices.aws()));
        response.setAzure(azureDataServicesDtoToResponse(dataServices.azure()));
        response.setGcp(gcpDataServicesDtoToResponse(dataServices.gcp()));
        return response;
    }

    private AwsDataServiceParameters awsDataServicesToDto(AwsDataServicesV1Parameters awsDataServices) {
        if (awsDataServices == null) {
            return null;
        }
        return AwsDataServiceParameters.builder().build();
    }

    private AzureDataServiceParameters azureDataServicesToDto(AzureDataServicesV1Parameters azureDataServices) {
        if (azureDataServices == null || StringUtils.isEmpty(azureDataServices.getSharedManagedIdentity())) {
            return null;
        }
        return AzureDataServiceParameters.builder()
                .withSharedManagedIdentity(azureDataServices.getSharedManagedIdentity())
                .build();
    }

    private GcpDataServiceParameters gcpDataServicesToDto(GcpDataServicesV1Parameters gcpDataServices) {
        if (gcpDataServices == null) {
            return null;
        }
        return GcpDataServiceParameters.builder().build();
    }

    private AwsDataServicesV1Parameters awsDataServicesDtoToResponse(AwsDataServiceParameters awsDataServicesDto) {
        if (awsDataServicesDto == null) {
            return null;
        }
        return new AwsDataServicesV1Parameters();
    }

    private AzureDataServicesV1Parameters azureDataServicesDtoToResponse(AzureDataServiceParameters azureDataServicesDto) {
        if (azureDataServicesDto == null || StringUtils.isEmpty(azureDataServicesDto.sharedManagedIdentity())) {
            return null;
        }
        AzureDataServicesV1Parameters azureDataServicesParameters = new AzureDataServicesV1Parameters();
        azureDataServicesParameters.setSharedManagedIdentity(azureDataServicesDto.sharedManagedIdentity());
        return azureDataServicesParameters;
    }

    private GcpDataServicesV1Parameters gcpDataServicesDtoToResponse(GcpDataServiceParameters gcpDataServicesDto) {
        if (gcpDataServicesDto == null) {
            return null;
        }
        return new GcpDataServicesV1Parameters();
    }
}