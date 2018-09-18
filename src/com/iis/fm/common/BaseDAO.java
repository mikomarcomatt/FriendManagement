package com.iis.fm.common;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import com.iis.fm.domain.BaseDomain;
import com.iis.fm.exception.DAOException;
import com.iis.fm.utils.ExtField;

public abstract class BaseDAO<T extends BaseDomain, ID extends Number> {

	private static final Logger log = Logger.getLogger(BaseDAO.class);

	private final SessionFactory sessionFactory = fm
			.getSessionFactory();

	private final Class clazz;
	private final String primaryFieldName;

	protected final SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	protected final Session getSession() {
		return getSessionFactory().getCurrentSession();
	}

	public BaseDAO() {
		ParameterizedType parameterizedType = (ParameterizedType) this
				.getClass().getGenericSuperclass();

		this.clazz = (Class) parameterizedType.getActualTypeArguments()[0];
		this.primaryFieldName = this.getPrimaryFieldName(clazz);
		if (primaryFieldName == null) {
			log.error("No primary field annotation for " + clazz);
		}
	}

	public void persist(T transientInstance) {
		log.debug("persisting instance "
				+ transientInstance.getClass().getName());
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		try {
			session.persist(transientInstance);
			transaction.commit();
			log.debug("persist successful");
		} catch (Exception re) {
			log.error("persist failed", re);
			transaction.rollback();
			throw re;
		}
	}

	

	public T findById(ID id) {
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.getTransaction();
		transaction.begin();
		try {
			T instance = (T) session.get(clazz, id);
			if (instance == null) {
				log.debug("get successful, no instance found");
			} 
			else {
				log.debug("getting instance : " + instance.getClass().getName()
						+ " with id " + id);
				log.debug("get successful, instance found");
			}
			transaction.commit();
			return instance;
		} catch (Exception re) {
			log.error("get failed", re);
			transaction.rollback();
			throw re;
		}
	}

	public List<T> findByExample(T instance) {
		log.debug("finding " + instance.getClass().getName()
				+ "instance by example");
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.getTransaction();
		transaction.begin();
		try {
			List<T> results = session.createCriteria(clazz)
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			transaction.commit();
			return results;
		} catch (Exception re) {
			log.error("find by example failed", re);
			transaction.rollback();
			throw re;
		}
	}
	
	
	public List<T> findAll() {
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.getTransaction();
		transaction.begin();
		try {
			List<T> results = session.createCriteria(clazz).list();
			transaction.commit();
			return results;
		} catch (Exception re) {
			log.error("find by example failed", re);
			transaction.rollback();
			throw re;
		}
	}

	public List<T> findAll(String sortName) {
		return findAll(sortName, SortDirection.ASC);
	}

	public List<T> findAll(String sortName, SortDirection sortDirection) {
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.getTransaction();
		transaction.begin();
		try {
			Criteria criteria = session.createCriteria(clazz);

			if (sortDirection == SortDirection.ASC) {
				criteria.addOrder(org.hibernate.criterion.Order.asc(sortName));
			} else {
				criteria.addOrder(org.hibernate.criterion.Order.desc(sortName));
			}

			List<T> results = criteria.list();
			transaction.commit();
			return results;
		} catch (Exception re) {
			log.error("find by example failed", re);
			transaction.rollback();
			throw re;
		}
	}

	
	
	protected final List<T> findByCriteria(String fieldName, Object fieldValue){
		return this.findByCriteria(Restrictions.eq(fieldName, fieldValue));
	}

	public List<T> findByCriteria(Criterion... exp) {
		return findByCriteria(Arrays.asList(exp));
	}
	
	public List<T> findByCriteria(String sortName, SortDirection sortDirection, Criterion... exp) {
		return findByCriteria(Arrays.asList(new SortInfo(sortName, sortDirection)),
				Arrays.asList(exp));
	}
	
	public List<T> findByCriteria(String sortName, SortDirection sortDirection, List<Criterion> exp) {
		return findByCriteria(Arrays.asList(new SortInfo(sortName, sortDirection)),
				exp);
	}

	public List<T> findByCriteria(List<SortInfo> sortInfos,
			Criterion... exp) {
		return findByCriteria(sortInfos, Arrays.asList(exp));
	}

	public List<T> findByCriteria(List<Criterion> exp) {
		return findByCriteria((List<SortInfo>)null, exp);
	}

	public List<T> findByCriteria(List<SortInfo> sortInfos,
			List<Criterion> exp) {
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.getTransaction();
		transaction.begin();
		try {
			Criteria criteria = session.createCriteria(clazz);
			if (exp != null) {
				for (Criterion o : exp) {
					criteria.add(o);
				}
			}
			
			if (sortInfos != null) {
				for (SortInfo info : sortInfos) {
					// org.hibernate.criterion.Order.
					if (info.getDirection() == SortDirection.ASC) {
						criteria.addOrder(org.hibernate.criterion.Order
								.asc(info.getProperty()));
					} else {
						criteria.addOrder(org.hibernate.criterion.Order
								.desc(info.getProperty()));
					}
				}
			}
			List<T> results = criteria.list();
			transaction.commit();
			return results;
		} catch (Exception re) {
			log.error("find by criteria failed", re);
			transaction.rollback();
			throw re;
		}
	}
	
	protected final T findByUnique(String fieldName, Object fieldValue){
		List<T> list = this.findByCriteria(Restrictions.eq(fieldName, fieldValue));
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		
		return null;
	}
	protected final T findByUnique(Criterion... exp){
		return findByUnique(Arrays.asList(exp));
	}
	
	protected final T findByUnique(String sortName, SortDirection sortDirection, Criterion... exp){
		return findByUnique(sortName, sortDirection, Arrays.asList(exp));
	}
	
	
	protected final T findByUnique(List<Criterion> exp){
		return findByUnique(null, null, exp);
	}
	protected final T findByUnique(String sortName, SortDirection sortDirection, List<Criterion> exp){
		List<T> list = null;
		if (sortName == null || sortDirection == null) {
			list = this.findByCriteria(exp);
		}
		else {
			list = this.findByCriteria(sortName, sortDirection, exp);
		}
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		
		return null;
	}
	
	//Paging
	public int getCount() {
		return getCount(Collections.EMPTY_LIST);
	}
	
	public int getCount(Criterion exp) {
		return getCount(Arrays.asList(exp));
	}
	
	public int getCount(List<Criterion> exp) {
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		try {
			Criteria criteria = session.createCriteria(clazz);
			if (exp != null) {
				for (Criterion o : exp) {
					criteria.add(o);
				}
			}
			Long count = (Long)criteria.setProjection(Projections.rowCount()).uniqueResult();
			transaction.commit();
			return count.intValue();
		}
		catch(Exception ex) {
			transaction.rollback();
			throw new DAOException(ex);
		}
	}
	
	public List<T> getPagingList(int startIndex, int pageSize) {
		return getPagingList(startIndex, pageSize, Collections.EMPTY_LIST);
	}
	
	public List<T> getPagingList(int startIndex, int pageSize, Criterion... exp) {
		return getPagingList(startIndex, pageSize, Arrays.asList(exp));
	}
	
	public List<T> getPagingList(int startIndex, int pageSize, List<Criterion> exp) {
		return getPagingList(startIndex, pageSize, null, null, exp);
	}
	
	public List<T> getPagingList(int startIndex, int pageSize, String sortName, SortDirection sortDirection, List<Criterion> exp) {
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.getTransaction();
		transaction.begin();
		try {
			Criteria criteria = session.createCriteria(clazz);
			criteria.setFirstResult(startIndex);
			criteria.setMaxResults(pageSize);
			if (exp != null) {
				for (Criterion o : exp) {
					criteria.add(o);
				}
			}
			if (sortDirection != null && sortName != null) {
				if (sortDirection == SortDirection.ASC) {
					criteria.addOrder(org.hibernate.criterion.Order.asc(sortName));
				} else {
					criteria.addOrder(org.hibernate.criterion.Order.desc(sortName));
				}
			}
			
			List<T> results = criteria.list();
			transaction.commit();
			return results;
		} catch (Exception re) {
			log.error("getPagingList failed", re);
			transaction.rollback();
			throw re;
		}
	}
	
	public List<ID> getPrimaryKeys() {
		String primaryKeyFieldName = getPrimaryFieldName();
		return this.getUniqueList(primaryKeyFieldName, Collections.EMPTY_LIST);
	}
	
	public ID getPrimaryKeyValue(T object) {
		String primaryFieldName = this.getPrimaryFieldName();
		String getMethod = "get" + Character.toUpperCase(primaryFieldName.charAt(0)) + primaryFieldName.substring(1);
		try {
			Method method = object.getClass().getDeclaredMethod(getMethod, null);
			return (ID)method.invoke(object, null);
		} 
		catch (Exception e) {
			throw new DAOException(e);
		} 
	}

	protected String getPrimaryFieldName() {
		if (primaryFieldName == null) {
			throw new DAOException("No primary field annotation for " + clazz);
		}
		return this.primaryFieldName;
	}
	
	private String getPrimaryFieldName(Class<?> clazz) {
		String primaryKeyFieldName = null;
		for (Field field : clazz.getDeclaredFields()) {
			ExtField extField = field.getAnnotation(ExtField.class);
			if (extField != null) {
				if (extField.primaryKey()) {
					primaryKeyFieldName = field.getName();
					break;
				}
			}
		}
		
		if (primaryKeyFieldName == null) {
			Class<?> superclazz = clazz.getSuperclass();
			if (superclazz != null) {
				primaryKeyFieldName = this.getPrimaryFieldName(superclazz);
			}
		}
		
		return primaryKeyFieldName;
	}
	
	public <E> List<E> getUniqueList(String fieldName, Criterion... exp) {
		return this.getUniqueList(fieldName, Arrays.asList(exp));
	}
	
	public <E> List<E> getUniqueList(String fieldName, List<Criterion> exp) {
		Session session = getSession();
		Transaction transaction = session.beginTransaction();
		try {
			Criteria criteria = session.createCriteria(clazz);
			criteria.setProjection(Projections.projectionList().add(Projections.property(fieldName)));
			if (exp != null) {
				for (Criterion o : exp) {
					criteria.add(o);
				}
			}
			List<E> list = criteria.list();
			transaction.commit();
			
			return list;
		} catch (Exception re) {
			transaction.rollback();
			throw re;
		}
	}
	
	
	
	public <E> E getMaxValue(String columnName) {
		return this.getMaxValue(columnName, (List<Criterion>)null);
	}
	
	public <E> E getMaxValue(String columnName, Criterion... exp) {
		return this.getMaxValue(columnName, Arrays.asList(exp));
	}
	
	public <E> E getMaxValue(String columnName, List<Criterion> exp) {
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.getTransaction();
		transaction.begin();
		try {
			Criteria criteria = session.createCriteria(clazz).setProjection(
					Projections.max(columnName));
			if (exp != null) {
				for (Criterion criterion : exp) {
					criteria.add(criterion);
				}
			}
			E value = (E) criteria.uniqueResult(); 
			transaction.commit();
			return value;
		}
		catch (Exception ex) {
			transaction.rollback();
			throw new DAOException(ex);
		}
	}
	
}
