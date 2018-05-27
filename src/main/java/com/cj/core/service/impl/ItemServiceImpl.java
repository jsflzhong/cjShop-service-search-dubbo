package com.cj.core.service.impl;

import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cj.core.mapper.ItemMapper;
import com.cj.core.pojo.SearchItem;
import com.cj.core.service.ItemService;
import com.cj.core.pojo.TaotaoResult;

@Service
public class ItemServiceImpl implements ItemService {

    //注入这个连接Solr服务所必须的SolrServer对象. 配置在IOC容器中的bean.
    @Autowired
    private SolrServer solrServer;
    @Autowired
    private ItemMapper itemMapper;

    /**
     * 查出数据库中的部分内容,保存进solr索引库.
     *
     * @return TaotaoResult
     * @throws Exception 2016年12月13日
     * @author cj
     */
    @Override
    public TaotaoResult importItems() throws Exception {
        //先查询数据库,返回商品SearchItem的集合.
        List<SearchItem> itemList = itemMapper.getItemList();
        //遍历列表
        for (SearchItem item : itemList) {
            //创建文档对象. 想把java对象写入Solr索引库,就得先把Java对象,转成SolrInputDocument对象.
            SolrInputDocument document = new SolrInputDocument();
            //添加域
            document.addField("id", item.getId());
            document.addField("item_title", item.getTitle());
            document.addField("item_sell_point", item.getSell_point());
            document.addField("item_price", item.getPrice());
            document.addField("item_image", item.getImage());
            document.addField("item_category_name", item.getCategory_name());
            document.addField("item_desc", item.getItem_desc());

            //写入索引库. 把封装好数据的"SolrInputDocument"对象.
            //这一步要求得抛异常.
            solrServer.add(document);
        }

        //别忘记提交
        solrServer.commit();
        //通过TaotaoResult,返回成功的信息.
        return TaotaoResult.ok();
    }

}
