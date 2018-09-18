Ext.define('fm.controller.friends', {
    extend: 'Ext.app.Controller',

    stores: ['friends'],

    models: ['friends'],

    views: ['FriendsList'],
    
    refs: [ 
            {
		ref: 'friendsPnl',
		selector: 'friends'
	},{
		ref: 'friendsList',
		selector: 'friendslist'
	}, {
		ref: 'searchButton',
		selector: 'friendslist button[action=search]'
	}, {
		ref: 'friendsForm',
		selector: 'friends form'
	}],

    init: function()
    {
        this.control({
            'friendlist button[action=seach]':
            {
                click: this.search
            },
            'friendlist': {
            	itemclick: this.rowclickFriends
            }
        });
        
       this.getFriendsStore().on('load', this.onStoreLoad, this);
    },
    
    seachFriends: function(button) 
    {
    	var form = this.getFriendsForm().getForm();
        record = form.getRecord();
        values = form.getValues();
    	if (values.emails == 0)
		{
			record = Ext.create('rims.model.Friends');
		}
    	record.set(values);
    	
    	form.submit({
    		scope: this,
    		success: function(form, action) {
    			this.getFriendList().store.load();
    			this.getFriendForm().getForm().reset();
    			//Ext.ux.window.Notification.info(i18n.successful, 'Successfully Found');
    		}
    	});
    },
    
    rowclickFriends: function(grid, record) {
    	var title = this.getEditPanelTitle();
    	var form = this.getFriendsForm().getForm();
		form.loadRecord(record);
		
		var masterObj = this.getFriendsPnl().getMasterData();
		var objectName = setMasterFormDetailsEdit(masterObj, record);
		
		this.getFriendsPanel().setTitle(objectName+' '+title + ' - Search');
		this.getFriendsPanel().up('#friendsWindow').setTitle(objectName+' '+title + ' - Search');
		
    },
    
   
    getEditPanelTitle: function() {
    	if (this.editFormPanelTitle == null) {
    		this.editFormPanelTitle = this.getAddressPanel().title;
    	}
    	return this.editFormPanelTitle;
    },
    
    onStoreLoad: function() {
    	//
    }
});
