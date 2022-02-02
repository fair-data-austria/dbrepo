package at.tuwien.mapper;

import at.tuwien.api.database.VersionDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.querystore.Version;
import org.apache.commons.codec.digest.DigestUtils;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StoreMapper.class);

    VersionDto versionToVersionDto(Version data);

    default Long queryResultDtoToLong(QueryResultDto data) {
        if (data == null) {
            return null;
        }
        return Long.parseLong(String.valueOf(data.getResult().size()));
    }

    default String queryResultDtoToString(QueryResultDto data) {
        if (data == null) {
            return null;
        }
        return DigestUtils.sha256Hex(data.getResult().toString());
    }

}
