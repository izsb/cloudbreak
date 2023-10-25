package com.sequenceiq.cloudbreak.api.endpoint.v4.imagecatalog.requests;

import static com.sequenceiq.cloudbreak.doc.ModelDescriptions.StackModelDescription.ENVIRONMENT_CRN;
import static com.sequenceiq.cloudbreak.doc.ModelDescriptions.StackModelDescription.IMAGE_SETTINGS;
import static com.sequenceiq.cloudbreak.doc.ModelDescriptions.StackModelDescription.REGION;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.request.image.ImageSettingsV4Request;
import com.sequenceiq.cloudbreak.doc.ModelDescriptions;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageRecommendationV4Request {

    @NotNull
    @ApiModelProperty(ModelDescriptions.CLOUD_PLATFORM)
    private String platform;

    @NotNull
    @ApiModelProperty(ModelDescriptions.BlueprintModelDescription.BLUEPRINT_NAME)
    private String blueprintName;

    @NotNull
    @ApiModelProperty(ENVIRONMENT_CRN)
    private String environmentCrn;

    @NotNull
    @ApiModelProperty(REGION)
    private String region;

    @ApiModelProperty(IMAGE_SETTINGS)
    private ImageSettingsV4Request image;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getBlueprintName() {
        return blueprintName;
    }

    public void setBlueprintName(String blueprintName) {
        this.blueprintName = blueprintName;
    }

    public String getEnvironmentCrn() {
        return environmentCrn;
    }

    public void setEnvironmentCrn(String environmentCrn) {
        this.environmentCrn = environmentCrn;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setImage(ImageSettingsV4Request image) {
        this.image = image;
    }

    public ImageSettingsV4Request getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "ImageRecommendationV4Request{" +
                "platform='" + platform + '\'' +
                ", blueprintName='" + blueprintName + '\'' +
                ", environmentCrn='" + environmentCrn + '\'' +
                ", region='" + region + '\'' +
                ", image=" + image +
                '}';
    }
}