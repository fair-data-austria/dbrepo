package at.tuwien.api.database.deposit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StatisticDto {

    private Long downloads;

    private Long views;

    private Long volume;

    @JsonProperty("unique_downloads")
    private Long uniqueDownloads;

    @JsonProperty("unique_views")
    private Long uniqueViews;

    @JsonProperty("version_views")
    private Long versionViews;

    @JsonProperty("version_volume")
    private Long versionVolume;

    @JsonProperty("version_downloads")
    private Long versionDownloads;

    @JsonProperty("version_unique_downloads")
    private Long versionUniqueDownloads;

    @JsonProperty("version_unique_views")
    private Long versionUniqueViews;

}
