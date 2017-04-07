package gr.ru.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;

import static com.google.common.base.Throwables.propagate;
import static gr.ru.utils.TestJsonUtil.jsonToMap;
import static gr.ru.utils.TestJsonUtil.jsonToSimpleList;
import static java.util.Collections.singletonMap;
import static java.util.regex.Pattern.quote;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.http.impl.client.HttpClients.createDefault;

/**
 *
 */
public class TestHttpUtil {

    public static class HttpResponse {
        private final String body;
        private final Map<String, List<String>> headers;
        private final int code;

        public HttpResponse(int code, String body, Map<String, List<String>> headers) {
            this.body = body;
            this.headers = headers;
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public String getBody() {
            return body;
        }

        public Map<String, Object> getBodyAsJson() {
            return jsonToMap(body);
        }

        public Set<String> getBodyAsSimpleSet() {
            return new HashSet<>(jsonToSimpleList(body));
        }


        public Map<String, List<String>> getFullHeaders() {
            return headers;
        }

        public Map<String, String> buildCookiesHeaders(final String... expectedCookies) {
            return singletonMap("Cookie", cookies(expectedCookies));
        }

        public String cookies(final String... expectedCookies) {
            final List<String> res = new ArrayList<>();

            final List<String> cookies = headers.get("Set-Cookie");

            if (cookies == null) {
                return null;
            }

            for (final String cookie : cookies) {
                for (String expectedCookie : expectedCookies) {
                    if (cookie.contains(expectedCookie)) {
                        res.add(cookie.split(quote(";"))[0]);
                    }
                }
            }
            return join(res, ";");
        }


        public Map<String, String> getHeaders() {
            final Map<String, String> res = new HashMap<>();
            for (String key : headers.keySet()) {
                List<String> list = headers.get(key);

                if (list.size() != 1) {
                    throw new IllegalArgumentException("several headers with single name!");
                }

                res.put(key, list.get(0));
            }
            return res;
        }

        public <T> T parseJson(final Class<T> type, final ObjectMapper mapper) throws IOException {
            return mapper.readValue(body, type);
        }
    }

    public static HttpResponse GET(final String url) {
        return GET(url, null);
    }

    public static HttpResponse GET(final String url, final Map<String, String> headers) {
        try (final CloseableHttpClient httpClient = createDefault()) {
            //формируем запрос
            HttpGet get = new HttpGet(url);
            if (headers != null) {
                for (String name : headers.keySet()) {
                    get.addHeader(name, headers.get(name));
                }
            }

            return getResponse(httpClient, get);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static HttpResponse POST(final String url) {
        return POST(url, "", null);
    }

    public static HttpResponse POST(final String url, final String body) {
        return POST(url, body, null);
    }

    public static HttpResponse POST(final String url, final String body, final Map<String, String> headers) {
        try (final CloseableHttpClient httpClient = createDefault()) {
            final HttpPost post = new HttpPost(url);
            post.setEntity(new StringEntity(body));
            if (headers != null) {
                for (String name : headers.keySet()) {
                    post.addHeader(name, headers.get(name));
                }
            }
            return getResponse(httpClient, post);
        } catch (IOException e) {
            throw propagate(e);
        }
    }

    private static HttpResponse getResponse(final CloseableHttpClient httpClient, final HttpRequestBase request) throws IOException {
        //формируем ответ
        try (final CloseableHttpResponse response = httpClient.execute(request)) {

            final Map<String, List<String>> respHeader = new HashMap<>();
            for (final Header header : response.getAllHeaders()) {

                if (!respHeader.containsKey(header.getName())) {
                    respHeader.put(header.getName(), new ArrayList<String>());
                }

                respHeader.get(header.getName()).add(header.getValue());

            }
            final HttpEntity entity = response.getEntity();
            final String body = entity != null ? EntityUtils.toString(entity) : null;
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                System.out.println(body);
            }

            return new HttpResponse(statusCode, body, respHeader);
        }
    }
}
