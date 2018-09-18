Ext.define('fm.store.friends', {
	extend: 'Ext.data.Store',
	model: 'fm.model.friends',
	autoLoad: false,
	remoteSort: true,
	remoteFilter: true,
	pageSize: 30,
	autoSync: false,
	sorters: [ {
		property: 'id',
		direction: 'ASC'
	} ]
});