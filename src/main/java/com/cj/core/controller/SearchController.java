package com.cj.core.controller;

import com.cj.common.pojo.TaotaoResult;
import com.cj.common.utils.ExceptionUtil;
import com.cj.core.pojo.SearchResult;
import com.cj.core.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author cj
 * @Describe 商品搜索服务的Controller
 * @Date 2018/5/18
 * @author cj
 */
@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 处理搜索请求的handler
     * 分页相关的参数都使用注解赋予了默认值.
     *
     * @param keyword 查询的条件
     * @param page    当前页码
     * @param rows    每页显示多少条
     * @return TaotaoResult
     * @author cj
     */
    @RequestMapping("/q")
    @ResponseBody
    public TaotaoResult search(@RequestParam(defaultValue = "") String keyword,
                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "30") Integer rows) {
        try {
            //转换字符集. 别忘记这步! 否则如果查询条件为中文的话,参数就会乱码! 结果就是什么也查不到!
            //keyword = new String(keyword.getBytes("iso8859-1"), "utf-8");
            //调用service层搜索.
            SearchResult searchResult = searchService.search(keyword, page, rows);
            //返回TaotaoResult,并把SearchResult对象包进去.
            return TaotaoResult.ok(searchResult);
        } catch (Exception e) {
            e.printStackTrace();
            //给调用者返回错误代码,和异常的值栈信息.
            return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
        }
    }
}
