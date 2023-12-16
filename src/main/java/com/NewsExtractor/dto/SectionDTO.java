package com.NewsExtractor.dto;

import com.NewsExtractor.entity.Article;
import com.NewsExtractor.entity.NewsPaper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Schema(name = "Section", description = "Structure of a section. \n " +
        "Only the source field and object newspaper are necessary.")
public class SectionDTO {
    @Schema(name = "ID", description = "Long variable as an identifier and is created implicitly.",
            requiredMode = Schema.RequiredMode.AUTO)
    private Long id;
    @Schema(name = "Name", description = "String variable as an name and is created implicitly.",
            requiredMode = Schema.RequiredMode.AUTO)
    private String name;
    @URL(message = "The source must have a url format")
    @Schema(name = "Source", description = "String variable as an source and " +
            "it is verified that it is a url.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String source;
    @Schema(name = "Number of total articles", description = "Integer variable as a " +
            "number of total articles of the list of the section and is created implicitly.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer total_articles;
    @Schema(name = "NewsPaper", requiredMode = Schema.RequiredMode.REQUIRED)
    private NewsPaper newsPaper;
    @Schema(name = "List of articles", description = "List variable with " +
            "article elements that is created implicitly.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<Article> articleList;
}
