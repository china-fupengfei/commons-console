/* __________              _____                                          *\
** \______   \____   _____/ ____\____   ____        Ponfee's code         **
**  |     ___/  _ \ /    \   __\/ __ \_/ __ \       (c) 2017-2018, MIT    **
**  |    |  (  <_> )   |  \  | \  ___/\  ___/       http://www.ponfee.cn  **
**  |____|   \____/|___|  /__|  \___  >\___  >                            **
**                      \/          \/     \/                             **
\*                                                                        */

package code.ponfee.console;

import java.util.HashMap;
import java.util.Map;

import code.ponfee.commons.collect.Collects;
import code.ponfee.commons.http.HttpParams;
import code.ponfee.commons.model.Page;
import code.ponfee.commons.model.PageHandler;
import code.ponfee.commons.model.PageRequestParams;
import code.ponfee.commons.util.MessageFormats;

/**
 * Build Pagination html page
 * 
 * @author Ponfee
 */
public final class PaginationHtmlBuilder {

    public static final String CDN_JQUERY = "<script src=\"http://libs.baidu.com/jquery/2.0.3/jquery.min.js\"></script>";

    private static final String PAGINATION_HTML = new StringBuilder(8192)
        .append("<!DOCTYPE html>                                                                                      \n")
        .append("<html>                                                                                               \n")
        .append("  <head lang=\"en\">                                                                                 \n")
        .append("    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />                        \n")
        .append("    <title>#{title}</title>                                                                          \n")
        .append("    <style>                                                                                          \n")
        .append("      * '{font-family: Microsoft YaHei;}'                                                            \n")
        .append("      .grid '{overflow-x: auto;background-color: #fff;color: #555;}'                                 \n")
        .append("      .grid table '{width: 100%;font-size: 12px;border-collapse: collapse;border-style: hidden;}'    \n")
        .append("      .grid table, div.grid table caption, div.grid table tr '{border: 1px solid #6d6d6d;}'          \n")
        .append("      .grid table tr td, div.grid table tr th '{border: 1px solid #6d6d6d;}'                         \n")
        .append("      .grid table caption '{                                                                         \n")
        .append("        font-size:14px; padding:5px;                                                                 \n")
        .append("        background:#e6e6fa; font-weight:bolder; border-bottom:none;                                  \n")
        .append("      }'                                                                                             \n")
        .append("      .grid table thead th '{padding: 5px;background: #ccc;}'                                        \n")
        .append("      .grid table td '{text-align: center;padding: 3px;}'                                            \n")
        .append("      .grid table td.text-left, .grid table th.text-left '{text-align:left;}'                        \n")
        .append("      .grid table td.text-right, .grid table th.text-right '{text-align:right;}'                     \n")
        .append("      .grid table td.text-center, .grid table th.text-center '{text-align:center;}'                  \n")
        .append("      .grid table tfoot th '{padding: 5px;}'                                                         \n")
        .append("      .grid table tr:nth-child(odd) td'{background:#fff;}'                                           \n")
        .append("      .grid table tr:nth-child(even) td'{background: #e8e8e8}'                                       \n")
        .append("      .grid p.remark '{font-size: 14px;}'                                                            \n")
        .append("      .grid .nowrap '{                                                                               \n")
        .append("        white-space:nowrap; word-break:keep-all;                                                     \n")
        .append("        overflow:hidden; text-overflow:ellipsis; max-width:200px;                                    \n")
        .append("      }'                                                                                             \n")
        .append("                                                                                                     \n")
        .append("      .container '{                                                                                  \n")
        .append("        background:#fdfdfd; padding:1rem; margin:3rem auto;                                          \n")
        .append("        border-radius:0.2rem; counter-reset:pagination; text-align:center;                           \n")
        .append("      }'                                                                                             \n")
        .append("      .container:after '{clear:both; content:\"\"; display:table;}'                                  \n")
        .append("      .container ul '{width:100%;}'                                                                  \n")
        .append("      .large '{width:45rem;}'                                                                        \n")
        .append("      .pagination ul, li '{list-style:none; display:inline; padding-left:0px;}'                      \n")
        .append("      .pagination li '{counter-increment:pagination;}'                                               \n")
        .append("      .pagination li:hover  a '{color:#fdfdfd; background-color:#1d1f20; border:solid 1px #1d1f20;}' \n")
        .append("      .pagination li.active a '{color:#fdfdfd; background-color:#1d1f20; border:solid 1px #1d1f20;}' \n")
        .append("      .pagination li:first-child a:after '{content:\"<\";}'                                          \n")
        .append("      .pagination li:nth-child(2) '{counter-reset:pagination;}'                                      \n")
        .append("      .pagination li:last-child a:after '{content:\"\";}'                                            \n")
        .append("      .pagination li a '{                                                                            \n")
        .append("        border:solid 1px #d6d6d6; border-radius:0.2rem; color:#7d7d7d; text-decoration:none;         \n")
        .append("        text-transform:uppercase; display:inline-block; text-align:center; padding:0.5rem 0.9rem;    \n")
        .append("      }'                                                                                             \n")
        .append("      .large li a '{display:none;}'                                                                  \n")
        .append("      .large li:first-child  a       '{display:inline-block;}'                                       \n")
        .append("      .large li:first-child  a:after '{content:\"<\";}'                                              \n")
        .append("      .large li:nth-child(2) a       '{display:inline-block;}'                                       \n")
        .append("      .large li:nth-child(3) a       '{display:inline-block;}'                                       \n")
        .append("      .large li:nth-child(4) a       '{display:inline-block;}'                                       \n")
        .append("      .large li:nth-child(5) a       '{display:inline-block;}'                                       \n")
        .append("      .large li:nth-child(6) a       '{display:inline-block;}'                                       \n")
        .append("      .large li:nth-child(7) a       '{display:inline-block;}'                                       \n")
        .append("      .large li:nth-child(8) a       '{display:inline-block;}'                                       \n")
        .append("      .large li:last-child a         '{display:inline-block;}'                                       \n")
        .append("      .large li:last-child a:after   '{content:\">\";}'                                              \n")
        .append("      .large li:nth-last-child(2) a  '{display:inline-block;}'                                       \n")
        .append("      .large li:nth-last-child(3)    '{display:inline-block;}'                                       \n")
        .append("    </style>                                                                                         \n")
        .append("    #{scripts}                                                                                       \n")
        .append("  </head>                                                                                            \n")
        .append("  <body>                                                                                             \n")
        .append("    <form method=\"GET\" name=\"search_form\" url=\"#{url}\" style=\"padding:5px;\">                 \n")
        .append("      #{form}                                                                                        \n")
        .append("      #{table}                                                                                       \n")
        .append("      <div class=\"container large\">                                                                \n")
        .append("        <div class=\"pagination\">                                                                   \n")
        .append("          <ul>                                                                                       \n")
        .append("            #{prevPage}                                                                              \n")
        .append("            <a href=\"javascript:void(0)\">#{pageNumBox}</a>                                         \n")
        .append("            #{nextPage}                                                                              \n")
        .append("          </ul>                                                                                      \n")
        .append("          (共【#{totalPages}】页, 每页【#{pageSizeBox}】条)                                           \n")
        .append("        </div>                                                                                       \n")
        .append("      </div>                                                                                         \n")
        .append("    </form>                                                                                          \n")
        .append("    #{foot}                                                                                          \n")
        .append("  </body>                                                                                            \n")
        .append("</html>                                                                                              \n")
        .toString().replaceAll(" +\n", "\n");

    public static String build(String title, String scripts, String form, String table, String url,
                               Page<?> page, PageRequestParams pageParams, String foot) {
        Map<String, Object> params = new HashMap<>(pageParams.getParams());
        params.remove(PageHandler.DEFAULT_PAGE_NUM);
        params.remove(PageHandler.DEFAULT_PAGE_SIZE);
        return build(title, scripts, form, table, url, page.getPageNum(),
                     page.getPageSize(), page.getPages(), params, foot);
    }

    public static String build(String title, String scripts, String form, 
                               String table, String url, int pageNum, int pageSize, 
                               int totalPages, Map<String, Object> params, String foot) {
        String _params = HttpParams.buildParams(params);
        Map<String, Object> map = Collects.toMap(
            "title",       title,
            "scripts",     scripts,
            "url",         url,
            "form",        form,
            "table",       table,
            "prevPage",    buildPageArrow(url, pageNum - 1, pageSize, totalPages, _params),
            "pageNumBox",  buildInputBox(PageHandler.DEFAULT_PAGE_NUM, pageNum),
            "nextPage",    buildPageArrow(url, pageNum + 1, pageSize, totalPages, _params),
            "totalPages",  totalPages,
            "pageSizeBox", buildInputBox(PageHandler.DEFAULT_PAGE_SIZE, pageSize),
            "foot",        foot
        );
        return MessageFormats.format(PAGINATION_HTML, map);
    }

    private static final String INPUT_BOX =
        "<input type=\"text\" name=\"#{name}\" value=\"#{value}\" style=\"width:40px;height:32px;text-align:center;margin:5px;\"/>";
    private static String buildInputBox(String name, Object value) {
        return MessageFormats.format(INPUT_BOX, name, value);
    }

    private static final String PAGE_ARROW = "<li><a href=\"#{url}?pageNum=#{pageNum}&pageSize=#{pageSize}&#{params}\"></a></li>";
    private static String buildPageArrow(String url, int pageNum, int pageSize, int totalPages, String params) {
        if (pageNum < 1 || pageNum > totalPages) {
            return "";
        }
        return MessageFormats.format(PAGE_ARROW, url, pageNum, pageSize, params);
    }

}
