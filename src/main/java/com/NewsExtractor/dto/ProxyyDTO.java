package com.NewsExtractor.dto;

import com.NewsExtractor.entity.NewsPaper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Schema(name = "Proxy", description = "Structure of a proxy. \n " +
        "All fields are necessary except for those created implicitly. " +
        "Although the status check is optional, no one will be able to use it if it is not available")
public class ProxyyDTO {
    @Schema(name = "ID", description = "Long variable as an identifier and is created implicitly.",
            requiredMode = Schema.RequiredMode.AUTO)
    private Long id;
    @Schema(name = "Name", description = "String variable as an name and is created implicitly.",
            requiredMode = Schema.RequiredMode.AUTO)
    private String name;
    @Schema(name = "Type", description = "String variable as an type. " +
            "There are three supported options, DIRECT, HTTP, SOCKS, " +
            "otherwise no one will be able to use it..",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;
    @Schema(name = "Address", description = "String variable as an Address.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;
    @Schema(name = "Port", description = "Integer variable as an port.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer port;
    @Schema(name = "State", description = "String variable as an state. " +
            "This number corresponds to the status code for a query to " +
            "the periodic object you have, where 200 is OK.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer state;
    @Schema(name = "NewsPaper", requiredMode = Schema.RequiredMode.REQUIRED)
    private NewsPaper newsPaper;

}
