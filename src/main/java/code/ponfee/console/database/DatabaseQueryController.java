package code.ponfee.console.database;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import code.ponfee.commons.collect.Collects;
import code.ponfee.commons.export.HtmlExporter;
import code.ponfee.commons.export.Table;
import code.ponfee.commons.export.Thead;
import code.ponfee.commons.export.Tmeta;
import code.ponfee.commons.export.Tmeta.Align;
import code.ponfee.commons.export.Tmeta.Type;
import code.ponfee.commons.http.ContentType;
import code.ponfee.commons.io.Files;
import code.ponfee.commons.model.Page;
import code.ponfee.commons.model.PageRequestParams;
import code.ponfee.commons.model.PaginationHtmlBuilder;
import code.ponfee.commons.model.Result;
import code.ponfee.commons.resource.ResourceLoaderFacade;
import code.ponfee.commons.web.WebUtils;

/**
 * Database query http api
 * 
 * @author Ponfee
 */
@RequestMapping("db/query")
@RestController
public class DatabaseQueryController {

    private @Value("${web.context.path:}") String contextPath;

    private @Resource DatabaseQueryService service;

    private final List<String> dataSourceNames;

    public DatabaseQueryController() throws IOException {
        Properties conf = new Properties();
        conf.load(ResourceLoaderFacade.getResource("application-jdbc.yml").getStream());
        Pattern pattern = Pattern.compile("^(.+)\\.jdbc\\.url$");
        conf.put("default.jdbc.url", "default.jdbc.url");
        dataSourceNames = conf.keySet().stream().map(key -> {
            Matcher matcher = pattern.matcher(key.toString().trim());
            return matcher.find() ? matcher.group(1) : null;
        }).filter(
            StringUtils::isNotBlank
        ).distinct().collect(Collectors.toList());
    }

    @GetMapping("page")
    public Result<Page<Object[]>> query4page(PageRequestParams params) {
        return Result.success(
            service.query4page(params).transform(Collects::map2array)
        );
    }

    // Oracle: select table_name from tabs
    // Mysql: select table_name from  INFORMATION_SCHEMA.TABLES
    @GetMapping("view")
    public void query4view(PageRequestParams params, HttpServletResponse resp) {
        Page<LinkedHashMap<String, Object>> page;
        String comment = null;
        try {
            page = service.query4page(params);
        } catch (Exception e) {
            page = Page.empty();
            comment = e.getMessage();
        }
        String[] head = CollectionUtils.isEmpty(page.getRows()) 
                        ? new String[] {"无查询结果"} 
                        : page.getRows().get(0).keySet().stream().toArray(String[]::new);
        AtomicInteger order = new AtomicInteger(1);
        List<Thead> heads = Arrays.stream(head).map(h -> new Thead(
            h, order.getAndIncrement(), 0, 
            new Tmeta(Type.CHAR, null, Align.LEFT, true, null)
        )).collect(Collectors.toList());
        Table table = new Table(heads);
        table.setComment(comment);
        page.process(row -> table.addRow(Collects.map2array(row)));
        table.end();
        try (HtmlExporter exporter = new HtmlExporter()) {
            exporter.build(table);
            PaginationHtmlBuilder builder = PaginationHtmlBuilder.newBuilder(
                "Database Query", contextPath + "/db/query/view", page
            );
            builder.table(exporter.body())
                   .scripts(PaginationHtmlBuilder.CDN_JQUERY)
                   .form(buildForm(params))
                   .params(params);

            WebUtils.response(resp, ContentType.TEXT_HTML.value(), builder.build(), Files.UTF_8);
        } 
    }

    private String buildForm(PageRequestParams params) {
        StringBuilder builder = new StringBuilder(2048)
            .append("<select name=\"datasource\">\n");
        for (String datasource : dataSourceNames) {
            builder.append("<option value=\"").append(datasource).append("\"")
                   .append(datasource(params.getString("datasource"), datasource))
                   .append(">").append(datasource).append("</option>\n");
        }
        return builder.append("</select>\n")
            .append("<input type=\"submit\" value=\"search\"/><br/>\n")
            .append("<textarea name=\"sql\" rows=\"8\" cols=\"100\">")
            .append(params.getString("sql")).append("</textarea>")
            .toString();
    }

    private String datasource(String actual, String expect) {
        return expect.equals(actual) ? " selected=\"selected\"" : "";
    }

}
