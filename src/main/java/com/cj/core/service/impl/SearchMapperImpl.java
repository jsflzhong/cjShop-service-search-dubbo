package com.cj.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cj.core.pojo.SearchItem;
import com.cj.core.pojo.SearchResult;
import com.cj.core.service.SearchMapper;

/**
 * Search服务的dao层
 * 从Solr服务器中,查询搜索结果的mapper.
 *
 * @author 崔健
 * @date 2016年12月16日上午12:20:40
 */
//别忘记加注解.
@Repository
public class SearchMapperImpl implements SearchMapper {

    //想要查询Solr索引库,必须注入SolrServer对象!!
    @Autowired
    private SolrServer solrServer;

    /**
     * 从Solr服务器中,查询搜索结果
     *
     * @param query 封装了查询条件.是apache的solr包里的类. 里面的数据从service层传入
     * @return SearchResult
     * @throws Exception Exception
     *                   2016年12月16日
     * @author cj
     */
    @Override
    public SearchResult search(SolrQuery query) throws Exception {
        //执行Solr查询
        QueryResponse response = solrServer.query(query);
        //取查询结果列表 . SolrDocumentList是一个ArrayList的子类.
        SolrDocumentList solrDocumentList = response.getResults();
        //创建一个集合,作为查询结果的列表. 就是商品数据的数据集了.
        List<SearchItem> itemList = new ArrayList<>();
        //迭代上面返回的itemList.
        for (SolrDocument solrDocument : solrDocumentList) {
            //创建一个SearchItem这个响应用pojo对象,其内封装了"商品数据"的各个字段,涉及到关联的三张表,记得么.
            SearchItem item = new SearchItem();
            //开始封装SearchItem的字段.
            //怎么看Solr索引库中的key的名字?
            //有俩方案:
            //1.去看schema.xml配置文件,我亲自给其中加的域.但是在Linux服务器上.
            //2.去http://192.168.172.134:8080/solr后台页面中,左边点Analysis,右边的下拉菜单中,就有所有的域的名字.
            item.setCategory_name((String) solrDocument.get("item_category_name"));//各种强转.
            item.setId((String) solrDocument.get("id"));
            item.setImage((String) solrDocument.get("item_image"));
            item.setPrice((Long) solrDocument.get("item_price"));
            item.setSell_point((String) solrDocument.get("item_sell_point"));
            item.setItem_desc((String) solrDocument.get("item_desc"));

            //取高亮显示...
            //看京东,搜索一个关键字,结果页面中,商品的"标题"中,才有关键字的高亮显示!
            //从上面的QueryResponse对象中,取高亮!
            /**
             高亮的原理: 把关键词从title中取出,加前缀后缀<em></em>,在网页中就可以有不同的颜色或粗细.

             进入Solr后台页面--Query--df:item_title(默认搜索域),q:手机,hl打钩,hl.fl:item_title(高亮显示的域)--execute query
             右边的结果,显示了数据的个数. 往下拉,有高亮的节点:"highlighting",我这里的代码getHighlighting()方法,取得就是这个节点.

             高亮节点的片段:

             "highlighting": {
             //第一层是Map,key是"id".
             "967021": {
                 //第二层也是个Map. key是:"高亮显示的域的名字". 就是上面输入的:hl.fl:item_title(高亮显示的域)
                 "item_title": [
                     //第三层是List.内容是:高亮后的,搜索的结果.
                     "TCL 老人<em>手机</em> (i310) 暗夜黑 移动联通2G<em>手机</em>"
                 ]
             },
             "1027857": {
                "item_title": [
                    "TCL 老人<em>手机</em> (i310) 纯净白 移动联通2G<em>手机</em>"
                ]
             },
             ...
             ...

             */
            //取上面json中的highlighting"节点的值.
            //注意,上面的highlighting"节点,默认是没有的.除非像上面那样在页面上打开jl单选框的勾;
            //或: 在代码中执行:query.setHighlight(true);该工作是在函数栈上层做的(SearchServiceImpl).
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
            //先get取第一层,即是id. 再get取第二层,即是 "高亮显示的域的名字".
            List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
            //先定义一个字符串.
            String itemTitle = "";
            //判断一下,上面取出的list是可能为空的.不判断就可能报错. 因为关键字可能不在item_title域中存在.
            //如果取到,就返回高亮的title,如果取不到,就从solrDocument中取普通的title.
            if (list != null && list.size() > 0) { //如果不为空,即有取到值.
                //则取高亮后的结果,只有一条,取第一个元素就是. 返回String类型.
                itemTitle = list.get(0);
            } else {//如果没取到值.
                //则正常取出title,不用高亮了. 只取JSON中的普通值,而不是高亮区的值.
                itemTitle = (String) solrDocument.get("item_title");
            }
            //把itemTitle封装进SearchItem对象的字段
            item.setTitle(itemTitle);

            //把SearchItem对象,添加到列表集合.
            itemList.add(item);
        }

        //最终要返回一个SearchResult对象.
        //因为它里面不仅有商品数据集,还有:总记录数, 总页数, 当前页码. 这些数据.
        SearchResult result = new SearchResult();
        /*
		 SearchResult这个pojo的字段:
			private List<SearchItem> itemList; //查询的结果集-即商品列表.
			private Long recordCount; //查询结果总记录数
			private int pageCount; //查询结果的总页数
			private int curPage; //当前页码
		*/
        //把上面的数据集,封装进SearchResult对象
        result.setItemList(itemList);
        //把查询结果的总记录数,封装进SearchResult对象
        //最上面查询Solr返回的solrDocumentList对象中,有这个获取数据记录数的方法.
        //其实就是.Solr后台页面中,Query--execute query--右边的"numFound"这个节点属性.
        result.setRecordCount(solrDocumentList.getNumFound());

        //返回这个SearchResult.
        return result;
    }

}
