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
@Schema(name = "Pattern", description = "Structure of a pattern. \n " +
        "Only the object  newspaper object and at least one field referring " +
        "to one location are necessary.")
public class PatternDTO {
    @Schema(name = "ID", description = "Long variable as an identifier and is created implicitly.",
            requiredMode = Schema.RequiredMode.AUTO)
    private Long id;
    @Schema(name = "Name", description = "String variable as an name and is created implicitly.",
            requiredMode = Schema.RequiredMode.AUTO)
    private String name;
    @Schema(name = "Location of the section", description = "String variable as " +
            "Location of the section using css query selectors.")
    private String loc_section;
    @Schema(name = "Location of the article", description = "String variable as " +
            "Location of the article in a section using css query selectors.")
    private String loc_per_article;
    @Schema(name = "Location of the title", description = "String variable as " +
            "Location of the title in an article using css query selectors.")
    private String loc_art_title;
    @Schema(name = "Location of the author", description = "String variable as " +
            "Location of the author in an article using css query selectors.")
    private String loc_art_authors;
    @Schema(name = "Location of the content", description = "String variable as " +
            "Location of the content in an article using css query selectors.")
    private String loc_art_content;
    @Schema(name = "NewsPaper", requiredMode = Schema.RequiredMode.REQUIRED)
    private NewsPaper newsPaper;
}
