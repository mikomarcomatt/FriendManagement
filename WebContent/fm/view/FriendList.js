Ext.define('fm.view.FriendList' ,{
    extend: 'Ext.grid.Panel',
    alias : 'widget.friendlist',
    
    requires: ['Ext.toolbar.Paging'],
    store: 'friends',
    stripeRows: true,
   
    
    plugins: [
        Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1,
            listeners:{
            	beforeedit: function( editor, e, eOpts ){
            		e.grid.friendsTypeData = storeFriendType; 		
            	}
            }
        })
    ],
    columns: 
    [
   {
		header: "email",
		width: 150,
		flex:1,
		dataIndex: 'emails'
	},
	
	],
	
	initComponent: function() {
		
		this.dockedItems = [
				{
			xtype : 'toolbar',
			dock : 'top',
			hidden: true,
			items : [	
			         {xtype: 'button', text: 'Search', iconCls: 'icon-search', width: 80}]
					},
					{
            xtype: 'pagingtoolbar',
            dock:'bottom',
            store: 'Friends',
            displayInfo: true,
            displayMsg: 'Displaying Friends {0} - {1} of {2}',
            emptyMsg: "No Friends to display"
        }];
		
		this.callParent(arguments);
	}
});
