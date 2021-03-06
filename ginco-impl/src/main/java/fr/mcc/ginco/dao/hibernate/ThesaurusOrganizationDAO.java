package fr.mcc.ginco.dao.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import fr.mcc.ginco.beans.Thesaurus;
import fr.mcc.ginco.beans.ThesaurusOrganization;
import fr.mcc.ginco.dao.IThesaurusOrganizationDAO;

/**
 * DAO for Thesaurus organization
 */
@Repository
public class ThesaurusOrganizationDAO extends
		GenericHibernateDAO<ThesaurusOrganization, Integer> implements
		IThesaurusOrganizationDAO {

	public ThesaurusOrganizationDAO() {
		super(ThesaurusOrganization.class);
	}

	@Override
	public List<ThesaurusOrganization> getFilteredOrganizationNames() {
		Criteria criteria = getCurrentSession().createCriteria(Thesaurus.class, "t")
				.add(Restrictions.isNotNull("t.creator"))
				.createCriteria("creator", "c", JoinType.RIGHT_OUTER_JOIN)
				.setProjection(
						Projections.distinct(Projections.projectionList().add(
								Projections.property("c.name").as("name"))))
				.setResultTransformer(Transformers.aliasToBean(ThesaurusOrganization.class))
				.addOrder(Order.asc("name"));
		return criteria.list();
	}
}
