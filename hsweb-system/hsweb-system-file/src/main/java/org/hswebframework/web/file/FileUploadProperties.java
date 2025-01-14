package org.hswebframework.web.file;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.hswebframework.utils.time.DateFormatter;
import org.hswebframework.web.id.IDGenerator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;

import java.io.File;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

@Getter
@Setter
@ConfigurationProperties(prefix = "hsweb.file.upload")
public class FileUploadProperties {

    private String staticFilePath = "./static";

    private String staticLocation = "/static";

    //是否使用原始文件名进行存储
    private boolean useOriginalFileName = false;

    private Set<String> allowFiles;

    private Set<String> denyFiles;

    private Set<String> allowMediaType;

    private Set<String> denyMediaType;

    public boolean denied(String name, MediaType mediaType) {
        String suffix = (name.contains(".") ? name.substring(name.lastIndexOf(".") + 1) : "").toLowerCase(Locale.ROOT);
        boolean defaultDeny = false;
        if (CollectionUtils.isNotEmpty(denyFiles)) {
            if (denyFiles.contains(suffix)) {
                return true;
            }
            defaultDeny = false;
        }

        if (CollectionUtils.isNotEmpty(allowFiles)) {
            if (allowFiles.contains(suffix)) {
                return false;
            }
            defaultDeny = true;
        }

        if (CollectionUtils.isNotEmpty(denyMediaType)) {
            if (denyMediaType.contains(mediaType.toString())) {
                return true;
            }
            defaultDeny = false;
        }

        if (CollectionUtils.isNotEmpty(allowMediaType)) {
            if (allowMediaType.contains(mediaType.toString())) {
                return false;
            }
            defaultDeny = true;
        }

        return defaultDeny;
    }

    public StaticFileInfo createStaticSavePath(String name) {
        String fileName = IDGenerator.SNOW_FLAKE_STRING.generate();
        String filePath = DateFormatter.toString(new Date(), "yyyyMMdd");

        //文件后缀
        String suffix = name.contains(".") ?
                name.substring(name.lastIndexOf(".")) : "";

        StaticFileInfo info = new StaticFileInfo();

        if (useOriginalFileName) {
            filePath = filePath + "/" + fileName;
            fileName = name;
        } else {
            fileName = fileName + suffix;
        }
        String absPath = staticFilePath.concat("/").concat(filePath);
        new File(absPath).mkdirs();

        info.location = staticLocation + "/" + filePath + "/" + fileName;
        info.savePath = absPath + "/" + fileName;

        return info;
    }

    @Getter
    @Setter
    public static class StaticFileInfo {
        private String savePath;

        private String location;
    }
}
