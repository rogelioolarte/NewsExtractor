package com.NewsExtractor.dto;

import com.NewsExtractor.entity.NewsPaper;
import com.NewsExtractor.entity.Section;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Schema(name = "Article", description = "Structure of an article. \n " +
        "Only the source field,  object section and object newspaper " +
        "are necessary, however other fields can be added.")
public class ArticleDTO {
    @Schema(name = "ID", description = "Long variable as an identifier and is created implicitly.",
            requiredMode = Schema.RequiredMode.AUTO)
    private Long id;
    @Schema(name = "Title", description = "String variable as an title.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String title;
    @URL(message = "The source must have a url format")
    @Schema(name = "Source", description = "String variable as an source and " +
            "it is verified that it is a url.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String source;
    @Schema(name = "Author", description = "String variable as an author.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String authors;
    @Schema(name = "Content", description = "String variable as content and " +
            "has a maximum length of 50000 characters.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String content;
    @Schema(name = "Section", requiredMode = Schema.RequiredMode.REQUIRED)
    private Section section;
    @Schema(name = "NewsPaper", requiredMode = Schema.RequiredMode.REQUIRED)
    private NewsPaper newsPaper;
}
