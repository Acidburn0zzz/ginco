Ext.define('GincoApp.controller.NotePanelController', {
	extend : 'Ext.app.Controller',
	localized : true,
	
	onRenderGrid : function(theGrid) {
		if (theGrid.up('conceptPanel') != null) {
			var theConceptId = theGrid.up('conceptPanel').conceptId;
			theGrid.getStore().getProxy().setExtraParam('conceptId', theConceptId);
		} else {
			//TODO
			var theTermId = theGrid.up('termPanel').termId;
			theGrid.getStore().getProxy().setExtraParam('termId', theTermId);
		}
		theGrid.getStore().load();
	},

	newNoteBtn : function(theButton){
		var win = Ext.create('GincoApp.view.CreateNoteWin');
		var theGrid = theButton.up('gridpanel');
		var theForm = win.down('form');
		var model = Ext.create('GincoApp.model.ThesaurusNoteModel');
		
		win.thesaurusData = theButton.up('conceptPanel').thesaurusData;
		win.store = theGrid.getStore();
		model.data.language=win.thesaurusData.languages[0];
		theForm.loadRecord(model);
		win.show();
	},
	
	loadLanguages : function(theCombo) {
		var thePanel = theCombo.up('createNoteWin');
		var theStore = theCombo.getStore();
		var theForm = thePanel.down('form');
		theStore.getProxy().setExtraParam('thesaurusId', thePanel.thesaurusData.id);
		theStore.load({
			callback: function (theStore, aOperation){
				var record = theForm.getRecord();
				record.data.language=thePanel.thesaurusData.languages[0];
				theForm.loadRecord(record);
			}
		});
	},
	
	saveNoteWinBtn : function(theButton) {
		var theForm = theButton.up('form');
		var theWin = theButton.up('createNoteWin');
		theForm.getForm().updateRecord();
		var updatedModel = theForm.getForm().getRecord();
		theWin.store.add(updatedModel);
		theWin.close();
	},
	
	saveNoteBtn : function(theButton) {
		var me = this;
		var theGrid = theButton.up('panel').down('gridpanel');
		theGrid.getStore().sync();
		
		//to implement : loading in progess and exception messages
	},

	init : function() {
		this.control({
			'notePanel gridpanel' : {
 				render : this.onRenderGrid
 			},
			'notePanel #saveNote' : {
 				click : this.saveNoteBtn
 			},
			'notePanel #newNoteBtn' : {
				click : this.newNoteBtn
			},
			'createNoteWin #languageCombo' : {
				render : this.loadLanguages
			},
			'createNoteWin #saveNote' : {
				click : this.saveNoteWinBtn
			}
		});
	}
});