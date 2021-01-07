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
     * ע������
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
        // Ŀ¼��������ھʹ���
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
    * ���ӿ�Content-Type�ǣ�application/x-www-form-urlencoded�������в��������Զ����б��룬���ն��յ���ϢҲ���Զ�����Content-Type���н��롣
    * ͬʱ���ӿ��в����ڷ��Ͷ˲�û�н��е�����URLEncode (sign��extReserved��sysReserved��������)�����ԣ��ڽ��ն˸���Content-Type����󣬼�Ϊԭʼ�Ĳ�����Ϣ��
    * ����HttpServletRequest��getParameter()�������ָ������ִ��������URLDecoder.decode(),���ԣ���Ӧ�����������������"%"���ͻᷢ������
    * ��ˣ����ǽ���ͨ�����·�����ȡԭʼ������Ϣ��
    * 
    * ע��ʹ�����·���������ԭʼServletRequestδ�����������½��У������޷���ȡ����Ϣ�����磬��Struts���������struts���Ѿ��Բ����������ɴ���
    * http��InputStream����ʵ�Ѿ�û����Ϣ����ˣ������������á�Ҫ��ȡԭʼ��Ϣ��������ԭʼ�ģ�δ�������ServletRequest�н��С�
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
        // �ӿ��У����²���sign��extReserved��URLEncode�ģ�������Ҫdecode����������ֱ����ԭʼ��Ϣ���ͣ�����Ҫdecode
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