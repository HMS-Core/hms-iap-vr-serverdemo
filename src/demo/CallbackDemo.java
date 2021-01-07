/**
Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package demo;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;

@SuppressWarnings("serial")
public class CallbackDemo extends HttpServlet {
    /**
     * 注释内容
     */
    private static final long serialVersionUID = 1L;
    public static final String devPubKey =
        "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKK9kzY3oGoRM3YZE04tYPXspSQDbfUduAN3E89v+Gu4ZuqUqOEstb4p7a01kEj8KwtyFUywH7cncygphQXcnRsCAwEAAQ==";

    /**
    * Constructor of the object.
    */
    public CallbackDemo() {
        super();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Map<String, Object> map = null;
        map = getValue(request);
        if (null == map)
            return;
        String sign = (String) map.get("sign");
        ResultDomain result = new ResultDomain();
        result.setResult(1);
        if (RSA.rsaDoCheck(map, sign, devPubKey, (String) map.get("signType"))) {
            result.setResult(0);
            System.out.println("Result : 0!");
        } else {
            result.setResult(1);
            System.out.println("Result : 1!");
        }
        String resultinfo = convertJsonStyle(result);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        System.out.println("Response string: " + resultinfo);
        PrintWriter out = response.getWriter();
        out.print(resultinfo);
        out.close();
    }

    public void writeFile(String filename, String content) throws Throwable {
        File file = new File(filename);
        // 目录如果不存在就创建
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream outSTr = new FileOutputStream(file, true);
        BufferedOutputStream Buff = new BufferedOutputStream(outSTr);
        Buff.write(content.getBytes());
        Buff.flush();
        Buff.close();
        outSTr.close();
    }

    /**
    * @param request
    * @return
    * 本接口Content-Type是：application/x-www-form-urlencoded，对所有参数，会自动进行编码，接收端收到消息也会自动根据Content-Type进行解码。
    * 同时，接口中参数在发送端并没有进行单独的URLEncode (sign和extReserved、sysReserved参数除外)，所以，在接收端根据Content-Type解码后，即为原始的参数信息。
    * 但是HttpServletRequest的getParameter()方法会对指定参数执行隐含的URLDecoder.decode(),所以，相应参数中如果包含比如"%"，就会发生错误。
    * 因此，我们建议通过如下方法获取原始参数信息。
    * 
    * 注：使用如下方法必须在原始ServletRequest未被处理的情况下进行，否则无法获取到信息。比如，在Struts情况，由于struts层已经对参数进行若干处理，
    * http中InputStream中其实已经没有信息，因此，本方法不适用。要获取原始信息，必须在原始的，未经处理的ServletRequest中进行。
    */
    public Map<String, Object> getValue(HttpServletRequest request) {
        /*
         * String path = request.getRealPath("/"); //path = path.substring(0, path.indexOf("webapps")); //path =
         * request.getContextPath(); if (path.endsWith(File.separator)) { path = path; }else { path = path +
         * File.separator; } path = path + "../../logs/my.log";
         */
        String line = null;
        StringBuffer sb = new StringBuffer();
        try {
            request.setCharacterEncoding("UTF-8");
            InputStream stream = request.getInputStream();
            InputStreamReader isr = new InputStreamReader(stream);
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\r\n");
            }
            System.out.println("The original data is : " + sb.toString());
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        String str = sb.toString();
        Map<String, Object> valueMap = new HashMap<String, Object>();
        if (null == str || "".equals(str)) {
            return valueMap;
        }
        String[] valueKey = str.split("&");
        for (String temp : valueKey) {
            if (temp != null) {
                int idx = temp.indexOf('=');
                int len = temp.length();
                if (idx != -1) {
                    String key = temp.substring(0, idx);
                    String value = idx + 1 < len ? temp.substring(idx + 1) : "";
                    valueMap.put(key, value);
                }
            }
        }
        System.out.println("The parameters in map are : " + valueMap);
        // 接口中，如下参数sign和extReserved是URLEncode的，所以需要decode，其他参数直接是原始信息发送，不需要decode
        try {
            String sign = (String) valueMap.get("sign");
            String extReserved = (String) valueMap.get("extReserved");
            String sysReserved = (String) valueMap.get("sysReserved");
            if (null != sign) {
                sign = URLDecoder.decode(sign, "utf-8");
                valueMap.put("sign", sign);
            }
            if (null != extReserved) {
                extReserved = URLDecoder.decode(extReserved, "utf-8");
                valueMap.put("extReserved", extReserved);
            }
            if (null != sysReserved) {
                sysReserved = URLDecoder.decode(sysReserved, "utf-8");
                valueMap.put("sysReserved", sysReserved);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valueMap;
    }

    private String convertJsonStyle(Object resultMessage) {
        ObjectMapper mapper = new ObjectMapper();
        Writer writer = new StringWriter();
        try {
            if (null != resultMessage) {
                mapper.writeValue(writer, resultMessage);
            }
        } catch (Exception e) {
        }
        return writer.toString();
    }
}