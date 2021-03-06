package com.saintdan.framework.component;

import com.saintdan.framework.po.User;
import com.saintdan.framework.vo.ObjectsVO;
import com.saintdan.framework.vo.PageVO;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Ids string transform to iterable helper.
 *
 * @author <a href="http://github.com/saintdan">Liao Yifan</a>
 * @date 10/19/15
 * @since JDK1.8
 */
@Component public class Transformer {

  // ------------------------
  // PUBLIC METHODS
  // ------------------------

  /**
   * Transform ids string to iterable.
   *
   * @param idsStr ids String
   * @return ids iterable
   */
  public Iterable<Long> idsStr2Iterable(String idsStr) {
    List<Long> ids = new ArrayList<>();
    // Transform ids string to ids array.
    String[] idsArray = idsStr.split(",");
    // Generate id list.
    for (String id : idsArray) {
      ids.add(Long.valueOf(id));
    }
    return ids;
  }

  /**
   * Transform object iterable to hash set.
   *
   * @param objects object iterable
   * @return object hash set
   */
  public <T> Set<T> iterable2Set(Iterable<T> objects) {
    Set<T> objectSet = new HashSet<>();
    for (T object : objects) {
      objectSet.add(object);
    }
    return objectSet;
  }

  /**
   * Transform PO page to PageVO.
   *
   * @param content       page content
   * @param pageable      page init
   * @param totalElements page total
   * @return PageVO
   */
  @SuppressWarnings("unchecked")
  public PageVO poPage2VO(List content, Pageable pageable, Long totalElements) {
    Page page = new PageImpl<>(content, pageable, totalElements);
    PageVO pageVO = new PageVO();
    pageVO.setPage(page);
    return pageVO;
  }

  /**
   * Transform VO list to VO of objects.
   *
   * @param list VO list
   * @return VO of objects
   */
  public ObjectsVO voList2ObjectsVO(List list) {
    ObjectsVO vo = new ObjectsVO();
    vo.setObjects(list);
    return vo;
  }

  /**
   * Transform param to PO.
   *
   * @param type        class type
   * @param param       param
   * @param po          PO
   * @param currentUser current user
   * @param <T>         class
   * @return PO
   * @throws Exception
   */
  public <T> T param2PO(Class<T> type, Object param, T po, User currentUser) throws Exception {
    // Init createdBy, lastModifiedBy
    Long createdBy;
    Long lastModifiedBy;
    // Init transformer
    Field idField = type.getDeclaredField("id");
    idField.setAccessible(true);
    Field createdByField = type.getDeclaredField("createdBy");
    createdByField.setAccessible(true);
    Field lastModifiedByField = type.getDeclaredField("lastModifiedBy");
    lastModifiedByField.setAccessible(true);
    // Log operation.
    if (idField.get(po) == null) {
      createdBy = currentUser.getId();
      lastModifiedBy = createdBy;
    } else {
      createdBy = (Long) createdByField.get(po);
      lastModifiedBy = currentUser.getId();
    }
    // Set param.
    BeanUtils.copyProperties(param, po);
    createdByField.set(po, createdBy);
    lastModifiedByField.set(po, lastModifiedBy);
    return po;
  }


  /**
   * Transform PO to VO.
   *
   * @param po PO
   * @return VO
   */
  public <T> T po2VO(Class<T> clazz, Object po) throws Exception {
    T vo = clazz.newInstance();
    BeanUtils.copyProperties(po, vo);
    return vo;
  }


  /**
   * Transform PO to VO
   *
   * @param pos PO
   * @return VO
   */
  public ObjectsVO pos2VO(Class<?> clazz, Iterable pos) throws Exception {
    List objList = poList2VOList(clazz, pos);
    return voList2ObjectsVO(objList);
  }

  /**
   * Transform PO list to VO list.
   *
   * @param pos PO list
   * @return VO list
   */
  @SuppressWarnings("unchecked")
  public List<?> poList2VOList(Class<?> type, Iterable pos) throws Exception {
    List voList = new ArrayList();
    for (Object po : pos) {
      Object vo = po2VO(type, po);
      voList.add(vo);
    }
    return voList;
  }

}
