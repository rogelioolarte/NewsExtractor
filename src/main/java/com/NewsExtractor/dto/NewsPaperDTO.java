package com.NewsExtractor.dto;

import com.NewsExtractor.entity.Article;
import com.NewsExtractor.entity.Pattern;
import com.NewsExtractor.entity.Proxyy;
import com.NewsExtractor.entity.Section;
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
@Schema(name = "NewsPaper", description = "Structure of a NewsPaper. \n " +
        "only the source field is required, all the others are created implicitly.")
public class NewsPaperDTO {
    @Schema(name = "ID", description = "Long variable as an identifier and is created implicitly.",
            requiredMode = Schema.RequiredMode.AUTO)
    private Long id;
    @Schema(name = "Name", description = "String variable as a name " +
            "is created when the object is added and is updated if possible when used.",
            requiredMode = Schema.RequiredMode.AUTO)
    private String name;
    @URL(message = "The source must have a url format")
    @Schema(name = "Source", description = "String variable as an source and " +
            "it is verified that it is a url.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String source;
    @Schema(name = "Number of total sections", description = "integer variable as the " +
            "total number of sections of the newspaper list and is created implicitly",
            requiredMode = Schema.RequiredMode.AUTO)
    private Integer total_sections;
    @Schema(name = "Number of total articles", description = "Integer variable as the " +
            "total number of articles in the newspaper list and is created implicitly.",
            requiredMode = Schema.RequiredMode.AUTO)
    private Integer total_articles;
    @Schema(name = "Progress", description = "String variable as extraction progress, " +
            "where it can have two states, Extraction Completed or Not Initialized and " +
            "is created implicitly.",
            requiredMode = Schema.RequiredMode.AUTO)
    private String progress;
    @Schema(name = "List of patterns", description = "List variable with " +
            "pattern elements that is created implicitly.",
            requiredMode = Schema.RequiredMode.AUTO)
    private List<Pattern> patternList;
    @Schema(name = "List of sections", description = "List variable with " +
            "section elements that is created implicitly.",
            requiredMode = Schema.RequiredMode.AUTO)
    private List<Section> sectionList;
    @Schema(name = "List of articles", description = "List variable with " +
            "article elements that is created implicitly.",
            requiredMode = Schema.RequiredMode.AUTO)
    private List<Article> articleList;
    @Schema(name = "List of proxy's", description = "List variable with " +
            "proxy elements that is created implicitly.",
            requiredMode = Schema.RequiredMode.AUTO)
    private List<Proxyy> proxyyList;
}
