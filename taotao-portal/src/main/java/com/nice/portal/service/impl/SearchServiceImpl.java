package com.nice.portal.service.impl;

import com.nice.common.utils.HttpClientUtil;
import com.nice.common.utils.TaotaoResult;
import com.nice.portal.pojo.SearchResult;
import com.nice.portal.service.SearchService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 商品搜索service
 * @author ningh
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Value("${SEARCH_BASE_URL}")
    private String SEARCH_BASE_URL;


    @Override
    public SearchResult search(String queryString, int page) {
        //调用taotao-search的服务
        //查询参数
        Map<String,String> param = new HashMap<>();
        param.put("q",queryString);
        param.put("page",page+"");
        try {
            //调用服务
            String json = HttpClientUtil.doGet(SEARCH_BASE_URL, param);
            //把字符串转换成java对象
            TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, SearchResult.class);
            if (taotaoResult.getStatus() == 200){
                SearchResult result = (SearchResult) taotaoResult.getData();
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
