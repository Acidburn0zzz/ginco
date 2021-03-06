/**
 * Copyright or © or Copr. Ministère Français chargé de la Culture
 * et de la Communication (2013)
 * <p/>
 * contact.gincoculture_at_gouv.fr
 * <p/>
 * This software is a computer program whose purpose is to provide a thesaurus
 * management solution.
 * <p/>
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software. You can use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * <p/>
 * As a counterpart to the access to the source code and rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty and the software's author, the holder of the
 * economic rights, and the successive licensors have only limited liability.
 * <p/>
 * In this respect, the user's attention is drawn to the risks associated
 * with loading, using, modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean that it is complicated to manipulate, and that also
 * therefore means that it is reserved for developers and experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systemsand/or
 * data to be ensured and, more generally, to use and operate it in the
 * same conditions as regards security.
 * <p/>
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fr.mcc.ginco.tests.audit.csv;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import fr.mcc.ginco.audit.csv.JournalEventsEnum;
import fr.mcc.ginco.audit.csv.JournalLine;
import fr.mcc.ginco.audit.csv.JournalLineBuilder;
import fr.mcc.ginco.beans.GincoRevEntity;
import fr.mcc.ginco.beans.ThesaurusConcept;
import fr.mcc.ginco.beans.ThesaurusTerm;
import fr.mcc.ginco.services.IThesaurusConceptService;
import fr.mcc.ginco.utils.DateUtil;

public class JournalLineBuilderTest {

	@Mock(name = "thesaurusConceptService")
	private IThesaurusConceptService thesaurusConceptService;

	@InjectMocks
	private JournalLineBuilder journalLineBuilder;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testBuildLineBase() {
		GincoRevEntity gincoRevEntity = new GincoRevEntity();
		gincoRevEntity.setUsername("fake.username");
		gincoRevEntity.setTimestamp(DateUtil.dateFromString(
				"2012-12-12 12:00:00").getTime());

		JournalLine actualBaseLine = journalLineBuilder.buildLineBase(
				JournalEventsEnum.THESAURUSTERM_CREATED, gincoRevEntity);

		Assert.assertEquals(DateUtil.dateFromString("2012-12-12 12:00:00"),
				actualBaseLine.getEventDate());
		Assert.assertEquals("fake.username", actualBaseLine.getAuthorId());
		Assert.assertEquals(JournalEventsEnum.THESAURUSTERM_CREATED,
				actualBaseLine.getEventType());

	}

	@Test
	public void testBuildTermAddedLine() {
		GincoRevEntity gincoRevEntity = new GincoRevEntity();
		gincoRevEntity.setUsername("fake.username");
		gincoRevEntity.setTimestamp(DateUtil.dateFromString(
				"2012-12-12 12:00:00").getTime());

		ThesaurusConcept concept = new ThesaurusConcept();
		concept.setIdentifier("fake-concept-id");

		ThesaurusTerm term = new ThesaurusTerm();
		term.setIdentifier("fake-term-id");
		term.setLexicalValue("One lexical value");
		term.setConcept(concept);

		Object[] revisionData = new Object[3];
		revisionData[0] = term;
		revisionData[1] = gincoRevEntity;

		JournalLine actualJournalLine = journalLineBuilder.buildTermAddedLine(
				(ThesaurusTerm) revisionData[0],
				(GincoRevEntity) revisionData[1]);

		Assert.assertEquals("fake-term-id", actualJournalLine.getTermId());
		Assert.assertEquals("One lexical value",
				actualJournalLine.getNewLexicalValue());
		Assert.assertEquals("fake-concept-id", actualJournalLine.getConceptId());

	}

	@Test
	public void testBuildTermLexicalValueChangedLine() {
		GincoRevEntity gincoRevEntity = new GincoRevEntity();
		gincoRevEntity.setUsername("fake.username");
		gincoRevEntity.setTimestamp(DateUtil.dateFromString(
				"2012-12-12 12:00:00").getTime());

		ThesaurusConcept concept = new ThesaurusConcept();
		concept.setIdentifier("fake-concept-id");

		ThesaurusTerm term = new ThesaurusTerm();
		term.setIdentifier("fake-term-id");
		term.setLexicalValue("One lexical value");
		term.setConcept(concept);

		JournalLine actualJournalLine = journalLineBuilder
				.buildTermLexicalValueChangedLine(term, gincoRevEntity,
						"Old lexical value");

		Assert.assertEquals("fake-term-id", actualJournalLine.getTermId());
		Assert.assertEquals("One lexical value",
				actualJournalLine.getNewLexicalValue());
		Assert.assertEquals("fake-concept-id", actualJournalLine.getConceptId());
		Assert.assertEquals("Old lexical value",
				actualJournalLine.getOldLexicalValue());

	}

	@Test
	public void testBuildTermRoleChangedLine() {
		GincoRevEntity gincoRevEntity = new GincoRevEntity();
		gincoRevEntity.setUsername("fake.username");
		gincoRevEntity.setTimestamp(DateUtil.dateFromString(
				"2012-12-12 12:00:00").getTime());

		ThesaurusConcept concept = new ThesaurusConcept();
		concept.setIdentifier("fake-concept-id");

		ThesaurusTerm term = new ThesaurusTerm();
		term.setIdentifier("fake-term-id");
		term.setConcept(concept);
		term.setPrefered(true);

		JournalLine actualJournalLine = journalLineBuilder
				.buildTermRoleChangedLine(term, gincoRevEntity);
		Assert.assertEquals("fake-term-id", actualJournalLine.getTermId());
		Assert.assertEquals("fake-concept-id", actualJournalLine.getConceptId());
		Assert.assertEquals("TP", actualJournalLine.getTermRole());

		term.setPrefered(false);
		JournalLine actualNonPreferredBaseLine = journalLineBuilder
				.buildTermRoleChangedLine(term, gincoRevEntity);
		Assert.assertEquals("TNP", actualNonPreferredBaseLine.getTermRole());

	}

	@Test
	public void testBuildTermAttachmentChangedLine() {
		GincoRevEntity gincoRevEntity = new GincoRevEntity();
		gincoRevEntity.setUsername("fake.username");
		gincoRevEntity.setTimestamp(DateUtil.dateFromString(
				"2012-12-12 12:00:00").getTime());

		ThesaurusConcept concept = new ThesaurusConcept();
		concept.setIdentifier("fake-concept-id");

		ThesaurusTerm term = new ThesaurusTerm();
		term.setIdentifier("fake-term-id");
		term.setLexicalValue("One lexical value");
		term.setConcept(concept);

		ThesaurusTerm preferredTerm = new ThesaurusTerm();

		JournalLine actualJournalLine = journalLineBuilder
				.buildTermAttachmentChangedLine(term, gincoRevEntity,
						preferredTerm);
		Assert.assertEquals("fake-term-id", actualJournalLine.getTermId());
		Assert.assertEquals("One lexical value",
				actualJournalLine.getNewLexicalValue());
		Assert.assertEquals("fake-concept-id", actualJournalLine.getConceptId());

	}

	@Test
	public void testBuildConceptHierarchyChanged() {
		GincoRevEntity gincoRevEntity = new GincoRevEntity();
		gincoRevEntity.setUsername("fake.username");
		gincoRevEntity.setTimestamp(DateUtil.dateFromString(
				"2012-12-12 12:00:00").getTime());

		ThesaurusConcept concept = new ThesaurusConcept();
		concept.setIdentifier("fake-concept-id");

		Set<ThesaurusConcept> oldParents = new HashSet<ThesaurusConcept>();
		ThesaurusConcept oldConcept1 = new ThesaurusConcept();
		oldConcept1.setIdentifier("oldConcept1");
		oldParents.add(oldConcept1);

		ThesaurusConcept oldConcept2 = new ThesaurusConcept();
		oldConcept2.setIdentifier("oldConcept2");
		oldParents.add(oldConcept2);

		Set<ThesaurusConcept> currentParents = new HashSet<ThesaurusConcept>();
		ThesaurusConcept curentConcept1 = new ThesaurusConcept();
		curentConcept1.setIdentifier("currentConcept1");
		currentParents.add(curentConcept1);

		ThesaurusConcept curentConcept2 = new ThesaurusConcept();
		curentConcept2.setIdentifier("currentConcept2");
		currentParents.add(curentConcept2);

		Mockito.when(thesaurusConceptService.getConceptLabel("oldConcept1"))
				.thenReturn("Old concept 1");
		Mockito.when(thesaurusConceptService.getConceptLabel("oldConcept2"))
				.thenReturn("Old concept 2");
		Mockito.when(thesaurusConceptService.getConceptLabel("currentConcept1"))
				.thenReturn("Current concept 1");
		Mockito.when(thesaurusConceptService.getConceptLabel("currentConcept2"))
				.thenReturn("Current concept 2");

		JournalLine actualLine = journalLineBuilder
				.buildConceptHierarchyChanged(concept, gincoRevEntity,
						oldParents, currentParents);

		Assert.assertEquals("fake-concept-id", actualLine.getConceptId());

		Assert.assertEquals(
				true,
				actualLine.getNewGenericTerm().contains(
						"Current concept 1 (currentConcept1)"));
		Assert.assertEquals(
				true,
				actualLine.getNewGenericTerm().contains(
						"Current concept 2 (currentConcept2)"));
		Assert.assertEquals(
				true,
				actualLine.getOldGenericTerm().contains(
						"Old concept 1 (oldConcept1)"));
		Assert.assertEquals(
				true,
				actualLine.getOldGenericTerm().contains(
						"Old concept 2 (oldConcept2)"));

	}

}
