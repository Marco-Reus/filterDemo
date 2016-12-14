package de.bvb.web.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
 * <p><b>Function:     处理全站乱码问题
 * </b></p>Class Name: CharacterEncodingFilter<br/>
 * Date:2016-12-13下午3:31:40<br/>author:Administrator<br/>since: JDK 1.6<br/>
 */
public class CharacterEncodingFilter implements Filter {
    private String charset = "UTF-8"; //默认编码设置为 UTF-8

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String charset = filterConfig.getInitParameter("characterEncoding");
        if (charset != null && !charset.equals("")) {
            this.charset = charset;
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        request.setCharacterEncoding(charset); //只能处理post方式的请求乱码
        response.setCharacterEncoding(charset);
        response.setContentType("text/html;charset=" + charset);
        chain.doFilter(new CharacterEncodingHttpServletRequest(request), response);
    }

    /**
     * 
     * <p><b>Function:     使用包装设计模式处理get方式的请求乱码
     * </b></p>Class Name: CharacterEncodingHttpServletRequest<br/>
     * Date:2016-12-13下午3:45:29<br/>author:Administrator<br/>since: JDK 1.6<br/>
     */
    class CharacterEncodingHttpServletRequest extends HttpServletRequestWrapper {
        private HttpServletRequest request;

        public CharacterEncodingHttpServletRequest(HttpServletRequest request) {
            super(request);
            this.request = request;
        }

        @Override
        public String getParameter(String name) {
            String value = request.getParameter(name);
            if (!"get".equalsIgnoreCase(request.getMethod())) { //如果是非get方法,直接返回
                return value;
            }
            if (value == null) {
                return null;
            }
            try {
                return value = new String(value.getBytes("iso8859-1"), request.getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void destroy() {
    }

}
