package com.iis.fm.service;

import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.FORM_POST;
import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.STORE_READ;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.bean.ExtDirectFormPostResult;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadResult;
import ch.ralscha.extdirectspring.bean.SortInfo;
import ch.ralscha.extdirectspring.filter.BooleanFilter;
import ch.ralscha.extdirectspring.filter.Filter;
import ch.ralscha.extdirectspring.filter.NumericFilter;
import ch.ralscha.extdirectspring.filter.StringFilter;

import com.iis.fm.common.BaseDAO;
import com.iis.fm.common.SortDirection;
import com.iis.fm.domain.BaseDomain;



public class BaseService<T extends BaseDomain, D extends BaseDAO> {
	
	private Class<D> daoClazz;
	private D daoInstance;

	public BaseService() {
		if (this.getClass().getGenericSuperclass() instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) this
					.getClass().getGenericSuperclass();
	
			daoClazz = (Class) parameterizedType.getActualTypeArguments()[1];
			try {
				daoInstance = daoClazz.newInstance();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	@ExtDirectMethod(STORE_READ)
	public List<T> loadAsLookups(HttpSession session, @Valid ExtDirectStoreReadRequest request)
	{
		if (daoInstance == null) {
			throw new RuntimeException("Please make the service to be parameterized.");
		}
		List<Criterion> criteria = getCriteria(request, session);
		if (criteria == null) {
			return daoInstance.findAllLookup();
		}

		return daoInstance.findByCriteria(criteria);
	}
	
	@ExtDirectMethod(STORE_READ)
	public List<T> load(HttpSession session, @Valid ExtDirectStoreReadRequest request)
	{
		if (daoInstance == null) {
			throw new RuntimeException("Please make the service to be parameterized.");
		}
		List<Criterion> criteria = getCriteria(request, session);
		
		List<SortInfo> sortInfos = request.getSorters();		
		SortDirection sortDirection = null;
		String sortName = null;
		if (!sortInfos.isEmpty()) {
			SortInfo sortInfo = sortInfos.get(0);
			sortName = sortInfo.getProperty();
			sortDirection = sortInfo.getDirection() == ch.ralscha.extdirectspring.bean.SortDirection.ASCENDING ? SortDirection.ASC : SortDirection.DESC;
		}

		return daoInstance.findByCriteria(sortName, sortDirection, criteria);
	}
	
	@ExtDirectMethod(STORE_READ)
	public ExtDirectStoreReadResult<T> loadPaging(HttpSession session, @Valid ExtDirectStoreReadRequest request) {
		if (daoInstance == null) {
			throw new RuntimeException("Please make the service to be parameterized.");
		}
		
		List<Criterion> criteria = getCriteria(request, session);
		List<SortInfo> sortInfos = request.getSorters();
		
		SortDirection sortDirection = null;
		String sortName = null;
		if (!sortInfos.isEmpty()) {
			SortInfo sortInfo = sortInfos.get(0);
			sortName = sortInfo.getProperty();
			sortDirection = sortInfo.getDirection() == ch.ralscha.extdirectspring.bean.SortDirection.ASCENDING ? SortDirection.ASC : SortDirection.DESC;
		}
		
		List<T> l = daoInstance.getPagingList(request.getStart(), request.getLimit(), sortName, sortDirection, criteria);
		int count = daoInstance.getCount(criteria);
		return new ExtDirectStoreReadResult<T>(count, l);
	}

	protected final List<Criterion> getCriteria(ExtDirectStoreReadRequest request, HttpSession session) {
		List<Filter> filters = request.getFilters();
		List<Criterion> criteria = null;
		if (!filters.isEmpty()) {
			criteria = new ArrayList<Criterion>();
			for (Filter filter : filters) {
				if (filter instanceof StringFilter) {
					criteria.add(getCriterionForStringFilter((StringFilter) filter));
				} 
				else if (filter instanceof NumericFilter)
				{
					criteria.add(getCriterionForNumericFilter((NumericFilter) filter));
				} 
				else if (filter instanceof BooleanFilter) {
					criteria.add(getCriterionForBooleanFilter((BooleanFilter) filter));
				} /*else if (filter instanceof DateFilter) {
					criteria.add(getCriterionForDateFilter((DateFilter) filter));
				}*/
				// For date, list and boolean will be considered later.
			}
		}
		
	
		return criteria;
	}
	
	protected Criterion getCriterionForBooleanFilter(BooleanFilter filter) {
		return Restrictions.eq(filter.getField(), filter.getValue());
	}
	

	protected Criterion getCriterionForStringFilter(StringFilter filter) {
		return Restrictions.ilike(filter.getField(), filter.getValue(), MatchMode.ANYWHERE);
	}
	
	protected Criterion getCriterionForNumericFilter(NumericFilter filter) {
		return Restrictions.eq(filter.getField(), filter.getValue().intValue());
	}
	

	
	protected final Integer getFriendsId(HttpSession session) {
		CustomUserDetails userDetails = getUser(session);
		return userDetails.getFriendId();
	}
	

	
	protected final D getDAO() {
		return daoInstance;
	}
	
	//Allow the sub class to overwrite for special parameter.
	protected <E> E getFilterValue(ExtDirectStoreReadRequest request, String filterName) {
		Filter filter = request.getFirstFilterForField(filterName);
		if (filter instanceof StringFilter) {
			return (E)((StringFilter) filter).getValue();
		}
		else if (filter instanceof NumericFilter) {
			Number value = ((NumericFilter) filter).getValue();
			//TODO Currently we only support for Integer. Need to find the way to support for other type.
			return (E)Integer.valueOf(value.intValue());
		}
		else if (filter instanceof BooleanFilter) {
			return (E) Boolean.valueOf(((BooleanFilter) filter).getValue());
		}
		return null;
	}
	


}
