package code.ponfee.console.database;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import code.ponfee.commons.collect.Collects;
import code.ponfee.commons.data.MultipleDataSourceContext;
import code.ponfee.commons.export.HtmlExporter;
import code.ponfee.commons.export.Table;
import code.ponfee.commons.export.Thead;
import code.ponfee.commons.export.Tmeta;
import code.ponfee.commons.export.Tmeta.Align;
import code.ponfee.commons.export.Tmeta.Type;
import code.ponfee.commons.http.ContentType;
import code.ponfee.commons.model.Page;
import code.ponfee.commons.model.PageRequestParams;
import code.ponfee.commons.model.PaginationHtmlBuilder;
import code.ponfee.commons.model.Result;
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

    @GetMapping("page")
    public Result<Page<Object[]>> query4page(PageRequestParams params) {
        return Result.success(
            service.query4page(params).transform(Collects::map2array)
        );
    }

    // Oracle: select table_name from tabs
    //  Mysql: select table_name from INFORMATION_SCHEMA.TABLES
    @GetMapping("view")
    public void query4view(PageRequestParams params, HttpServletResponse resp) {
        Page<LinkedHashMap<String, Object>> page;
        String errorMsg = null;
        try {
            page = service.query4page(params);
        } catch (Exception e) {
            page = Page.empty();
            errorMsg = e.getMessage();
        }
        Stream<String> head = CollectionUtils.isEmpty(page.getRows()) 
                            ? Arrays.stream(new String[] { "无查询结果" })
                            : page.getRows().get(0).keySet().stream();
        AtomicInteger order = new AtomicInteger(1);
        List<Thead> heads = head.map(h -> new Thead(
            h, order.getAndIncrement(), 0, 
            new Tmeta(Type.CHAR, null, Align.LEFT, true, null)
        )).collect(Collectors.toList());

        Table table = new Table(heads);
        table.setComment(errorMsg);
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

            WebUtils.response(resp, ContentType.TEXT_HTML, 
                              builder.build(), StandardCharsets.UTF_8);
        } 
    }

    private String buildForm(PageRequestParams params) {
        StringBuilder builder = new StringBuilder(2048)
            .append("<select name=\"datasource\">\n");
        for (String datasource : MultipleDataSourceContext.getDataSourceKeys()) {
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
