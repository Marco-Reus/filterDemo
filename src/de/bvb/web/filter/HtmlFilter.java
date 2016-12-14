package de.bvb.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * <p><b>Function:     html标签过滤器
 * </b></p>Class Name: HtmlFilter<br/>
 * Date:2016-12-13下午5:14:26<br/>author:Administrator<br/>since: JDK 1.6<br/>
 */
public class HtmlFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        chain.doFilter(new HtmlHttpServletRequestWrapper(request), response);
    }

    class HtmlHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private HttpServletRequest request;

        public HtmlHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
            this.request = request;
        }

        @Override
        public String getParameter(String name) {
            return filter(request.getParameter(name));
        }

        public String filter(String message) {
            if (message == null)
                return (null);
            char content[] = new char[message.length()];
            message.getChars(0, message.length(), content, 0);
            StringBuffer result = new StringBuffer(content.length + 50);
            for (int i = 0; i < content.length; i++) {
                switch (content[i]) {
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                case '&':
                    result.append("&amp;");
                    break;
                case '"':
                    result.append("&quot;");
                    break;
                default:
                    result.append(content[i]);
                }
            }
            return (result.toString());
        }

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

}
