import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 沈锋
 * @date 2019/8/24
 */
public class Main {

    public static final int PAGE=1000;
    public static final Long SLEEP_TIME=1L;
    public static final String OUTPUT_PATH="d:\\";
    public static final String[] URLS =new String[]{
            "http://www.120ask.com/list/xhnk/",//消化内科
            "http://www.120ask.com/list/hxnk/",//呼吸内科
            "http://www.120ask.com/list/nfmk/",//内分泌科
            "http://www.120ask.com/list/xynk/",//血液内科
            "http://www.120ask.com/list/xxgnk/"//心血管内科
    };

    public static List<List<String>> values=new ArrayList<>();
    public static void main(String[] args) throws IOException {
        int i=1;
        for (String url: URLS) {
            values=new ArrayList<>();
            Start(url,i);
            i++;
            String[] title = new String[]{
                    "科室","疾病","问题","描述","回答1","回答2","回答3","回答4"
            };
            ExcelUtils.getHSSFWorkbook("数据",title,values,null);
        }
    }

    private static void Start(String url,int indexOfType) throws IOException {
        for(int i=1; i<=PAGE;i++){
            System.out.println("开始收集第"+indexOfType+"种"+"第"+i+"页");
            List<String> urlList=getUrlListByUrl(i==1?url:url+i+"/");
            urlList.forEach(Main::GetData);
        }
    }

    public static List<String> getUrlListByUrl(String url) throws IOException {
        List<String> urlList=new ArrayList<>();
        Document doc = getDocumet(url);
        if(doc!=null){
            Elements list= doc.getElementsByClass("q-quename");
            for (Element item :list) {
                urlList.add("https:"+item.attr("href"));
            }
        }
        return urlList;
    }

    /**
     * 通过url 获取 Document
     * @param url
     * @return
     * @throws IOException
     */
    public static Document getDocumet(String url) {
        try {
            return Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.90 Safari/537.36").get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 测试用获取数据打印
     * @param url
     * @return
     */
    public static void GetData(String url){
        List<String> result=new ArrayList<>();
        Document document=getDocumet(url);
        if(document!=null){
            Elements a = document.select(".b_route").first().children();
            String jibing=a.eq(a.size()-1).text();
            String dept=a.eq(a.size()-2).text();
            String title=document.select("#d_askH1").first().text();
            Element contentDiv=document.select(".crazy_new").first();
            String content=contentDiv.text().replace(contentDiv.select("span").outerHtml(),"").replace("健康咨询描述：","");
            String answer1=getAnswer(document,0);
            String answer2=getAnswer(document,1);
            String answer3=getAnswer(document,2);
            String answer4=getAnswer(document,3);
            result.add(dept);
            result.add(jibing);
            result.add(title);
            result.add(content);
            result.add(answer1);
            result.add(answer2);
            result.add(answer3);
            result.add(answer4);
            values.add(result);
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取答案
     * @param document html 文档
     * @param i 第几个
     * @return
     */
    private static String getAnswer(Document document, int i) {
        String answer=null;
        try {
            answer= document.select(".b_anscont_cont").eq(i).first().select("p").first().text();
        }catch (Exception e){
            e.printStackTrace();
        }
        return answer;
    }

}
