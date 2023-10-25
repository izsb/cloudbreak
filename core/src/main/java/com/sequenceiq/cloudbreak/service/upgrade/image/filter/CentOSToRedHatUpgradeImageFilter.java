package com.sequenceiq.cloudbreak.service.upgrade.image.filter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.auth.ThreadBasedUserCrnProvider;
import com.sequenceiq.cloudbreak.auth.altus.EntitlementService;
import com.sequenceiq.cloudbreak.cloud.model.catalog.Image;
import com.sequenceiq.cloudbreak.service.upgrade.image.ImageFilterParams;
import com.sequenceiq.cloudbreak.service.upgrade.image.ImageFilterResult;
import com.sequenceiq.cloudbreak.util.VersionComparator;

@Component
public class CentOSToRedHatUpgradeImageFilter implements UpgradeImageFilter {

    public static final String REDHAT_8 = "redhat8";

    public static final String REDHAT_7 = "redhat7";

    public static final String CENTOS_7 = "centos7";

    public static final String VERSION_7_2_17 = "7.2.17";

    private static final Logger LOGGER = LoggerFactory.getLogger(CentOSToRedHatUpgradeImageFilter.class);

    private static final int ORDER_NUMBER = 11;

    @Inject
    private EntitlementService entitlementService;

    @Override
    public ImageFilterResult filter(ImageFilterResult imageFilterResult, ImageFilterParams imageFilterParams) {
        String currentOs = imageFilterParams.getCurrentImage().getOs();
        String currentOsType = imageFilterParams.getCurrentImage().getOsType();
        List<Image> filteredImages = filterImages(imageFilterParams, imageFilterResult, currentOs, currentOsType);
        LOGGER.debug("After the filtering {} image left with proper OS {} and OS type {}.", filteredImages.size(), currentOs, currentOsType);
        return new ImageFilterResult(filteredImages, getReason(filteredImages, imageFilterParams));
    }

    @Override
    public String getMessage(ImageFilterParams imageFilterParams) {
        return "There are no eligible images to upgrade.";
    }

    @Override
    public Integer getFilterOrderNumber() {
        return ORDER_NUMBER;
    }

    private List<Image> filterImages(ImageFilterParams imageFilterParams, ImageFilterResult imageFilterResult, String currentOs, String currentOsType) {
        boolean rhel8ImagePreferred = entitlementService.isRhel8ImagePreferred(ThreadBasedUserCrnProvider.getAccountId());
        List<Image> images = imageFilterResult.getImages()
                .stream()
                .filter(image -> {
                    if (!isCentOSImage(currentOs, currentOsType)) {
                        return true;
                    }
                    if (isRedHatImage(image.getOs(), image.getOsType())) {
                        return rhel8ImagePreferred && isCentOSToRedHatUpgradableVersion(imageFilterParams.getCurrentImage(), image);
                    }
                    return true;
                })
                .collect(Collectors.toList());
        if (isCentOSImage(currentOs, currentOsType) && images.stream().anyMatch(image -> isRedHatImage(image.getOs(), image.getOsType()))) {
            return images.stream().filter(image -> isRedHatImage(image.getOs(), image.getOsType())).toList();
        } else {
            return images;
        }
    }

    public static boolean isCentOSToRedHatUpgradableVersion(com.sequenceiq.cloudbreak.cloud.model.Image currentImage, Image image) {
        String currentStackVersion = currentImage.getPackageVersions().get("stack");
        String imageStackVersion = image.getVersion();
        VersionComparator versionComparator = new VersionComparator();
        return Objects.equals(currentStackVersion, imageStackVersion) && versionComparator.compare(() -> VERSION_7_2_17, () -> currentStackVersion) <= 0;
    }

    private boolean isRedHatImage(String os, String osType) {
        return REDHAT_8.equalsIgnoreCase(os) && REDHAT_8.equalsIgnoreCase(osType);
    }

    private boolean isCentOSImage(String os, String osType) {
        return CENTOS_7.equalsIgnoreCase(os) && REDHAT_7.equalsIgnoreCase(osType);
    }

    public static boolean isCentOSToRedhatUpgrade(String currentOs, String currentOsType, Image newImage) {
        return currentOs.equalsIgnoreCase(CENTOS_7) &&
                currentOsType.equalsIgnoreCase(REDHAT_7) &&
                newImage.getOs().equalsIgnoreCase(REDHAT_8) &&
                newImage.getOsType().equalsIgnoreCase(REDHAT_8);
    }
}