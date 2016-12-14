package de.bvb.web.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * <p><b>Function:     敏感词过滤器
 * </b></p>Class Name: SensitiveWordFilter<br/>
 * Date:2016-12-13下午5:17:56<br/>author:Administrator<br/>since: JDK 1.6<br/>
 */
public class SensitiveWordFilter implements Filter {
    /** 敏感词 */
    private List<String> banWordList = new ArrayList<String>();
    /** 审核词 */
    private List<String> authWordList = new ArrayList<String>();
    /** 替换词 */
    private List<String> replaceWordList = new ArrayList<String>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            String path = SensitiveWordFilter.class.getClassLoader().getResource("words").getPath();
            File[] files = new File(path).listFiles();
            for (File file : files) {
                if (!file.getName().endsWith(".txt")) {
                    continue;
                }
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = null;
                while ((line = br.readLine()) != null) {
                    String s[] = line.split("\\|");
                    if (s.length != 2) {
                        continue;
                    }
                    if (s[1].trim().equals("1")) {
                        banWordList.add(s[0].trim());
                    }

                    if (s[1].trim().equals("2")) {
                        authWordList.add(s[0].trim());
                    }

                    if (s[1].trim().equals("3")) {
                        replaceWordList.add(s[0].trim());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        //禁用词 检查
        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String name = (String) enumeration.nextElement();
            String value = request.getParameter(name);
            for (String s : banWordList) {
                Matcher matcher = Pattern.compile(s).matcher(value);
                if (matcher.find()) {
                    request.setAttribute("message", "文章中包括禁用词,请检查后再提交");
                    request.getRequestDispatcher("/WEB-INF/page/message.jsp").forward(request, response);
                    return;
                }
            }
        }
        chain.doFilter(new SensitiveWordHttpServletRequestWrapper(request), response);
    }

    class SensitiveWordHttpServletRequestWrapper extends HttpServletRequestWrapper {
        private HttpServletRequest request;

        public SensitiveWordHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
            this.request = request;
        }

        @Override
        public String getParameter(String name) {
            String value = request.getParameter(name);
            if (value == null) {
                return null;
            }
            //审核词 检查
            for (String s : authWordList) {
                Matcher matcher = Pattern.compile(s).matcher(value);
                if (matcher.find()) {
                    value = value.replaceAll(s, "<font color='red'>" + matcher.group() + "</font>");
                }
            }
            //替换词 检查
            for (String s : replaceWordList) {
                Matcher matcher = Pattern.compile(s).matcher(value);
                if (matcher.find()) {
                    value = value.replaceAll(s, "*****");
                }
            }
            return value;
        }
    }

    @Override
    public void destroy() {
    }

}
