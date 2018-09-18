Ext.define('fm.view.Friends' ,{
	extend: 'Ext.panel.Panel',
    alias : 'widget.friends',
    modal: true,
    height: 547,
    title: 'Friends List',
    layout : {
		type : 'vbox',
		align: 'stretch'
	},
	items : [
		{
			collapsible : false,
			xtype: 'friendslist',
			flex: 1
		},
		{
			xtype: 'friendsearch'
		}
	],
	
	setMasterData : function(record) {
		master = record;
		var store = this.down('friendslist').getStore();
		store.filters.clear();
		store.filters.add({property: 'Id', value: record.data.objectTypeId});
		store.filters.add({property: 'masterId', value: getMasterId(record)});
		store.load({
			scope: this,
		     callback: function (records, operation, success) {
		    	 
		     }
		 });
	},

	getMasterData : function() {
		return master;
	}
});
