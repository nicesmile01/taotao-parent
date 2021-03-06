package com.nice.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nice.common.pojo.EUDataGridResult;
import com.nice.common.utils.IDUtils;
import com.nice.common.utils.TaotaoResult;
import com.nice.mapper.TbItemDescMapper;
import com.nice.mapper.TbItemMapper;
import com.nice.mapper.TbItemParamItemMapper;
import com.nice.pojo.TbItem;
import com.nice.pojo.TbItemDesc;
import com.nice.pojo.TbItemExample;
import com.nice.pojo.TbItemParamItem;
import com.nice.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author ningh
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbItemDescMapper itemDescMapper;

    @Autowired
    private TbItemParamItemMapper itemParamItemMapper;

    @Override
    public TbItem getItemById(long itemId) {
//        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        TbItemExample example = new TbItemExample();
        //添加查询条件
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(itemId);
        List<TbItem> list = itemMapper.selectByExample(example);
        if (list != null && list.size() > 0 ){
            TbItem item = list.get(0);
            return item;
        }
        return null;
    }

    /**
     * 商品列表查询
     * @param page
     * @param rows
     * @return
     */
    @Override
    public EUDataGridResult getItemList(int page, int rows) {
        //查询商品列表
        TbItemExample example = new TbItemExample();
        //分页处理
        PageHelper.startPage(page,rows);
        List<TbItem> list = itemMapper.selectByExample(example);
        //创建一个返回值对象
        EUDataGridResult result = new EUDataGridResult();
        result.setRows(list);
        //取记录总条数
        PageInfo<TbItem> pageInfo = new PageInfo<>(list);
        result.setTotal(pageInfo.getTotal());
        return result;
    }

    @Override
    public TaotaoResult createItem(TbItem item,String desc,String itemParam) throws Exception {
        //item补全
        //生成商品ID
        Long itemId = IDUtils.genItemId();
        item.setId(itemId);
        //商品状态 1正常 2下架 3删除
        item.setStatus((byte) 1);
        item.setCreated(new Date());
        item.setUpdated(new Date());
        //插入到数据库
        itemMapper.insert(item);
        //添加商品描述信息
        TaotaoResult result = insertItemDesc(itemId, desc);
        //添加规格参数
        if (result.getStatus() != 200){
            throw new Exception();
        }
        result = inserItemParamItem(itemId, itemParam);
        if (result.getStatus() != 200){
            throw new Exception();
        }
        return TaotaoResult.ok();
    }

    /**
     * 添加商品描述
     * @param itemId
     * @param desc
     * @return
     */
    private TaotaoResult insertItemDesc(Long itemId, String desc){
        TbItemDesc itemDesc = new TbItemDesc();
        itemDesc.setItemId(itemId);
        itemDesc.setItemDesc(desc);
        itemDesc.setCreated(new Date());
        itemDesc.setUpdated(new Date());
        itemDescMapper.insert(itemDesc);
        return TaotaoResult.ok();
    }

    /**
     * 添加规格参数
     * @param itemId
     * @param itemParam
     * @return
     */
    private TaotaoResult inserItemParamItem(Long itemId,String itemParam){
        //创建一个pojo
        TbItemParamItem itemParamItem = new TbItemParamItem();
        itemParamItem.setItemId(itemId);
        itemParamItem.setParamData(itemParam);
        itemParamItem.setCreated(new Date());
        itemParamItem.setUpdated(new Date());
        //向表中插入数据
        itemParamItemMapper.insert(itemParamItem);
        return TaotaoResult.ok();
    }
}
