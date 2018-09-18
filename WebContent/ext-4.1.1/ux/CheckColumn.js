/**
 * @class Ext.ux.CheckColumn
 * @extends Ext.grid.column.Column
 * A Header subclass which renders a checkbox in each column cell which toggles the truthiness of the associated data field on click.
 *
 * Example usage:
 * 
 *    // create the grid
 *    var grid = Ext.create('Ext.grid.Panel', {
 *        ...
 *        columns: [{
 *           text: 'Foo',
 *           ...
 *        },{
 *           xtype: 'checkcolumn',
 *           text: 'Indoor?',
 *           dataIndex: 'indoor',
 *           width: 55
 *        }]
 *        ...
 *    });
 *
 * In addition to toggling a Boolean value within the record data, this
 * class adds or removes a css class <tt>'x-grid-checked'</tt> on the td
 * based on whether or not it is checked to alter the background image used
 * for a column.
 */
Ext.define('Ext.ux.CheckColumn', {
    extend: 'Ext.grid.column.Column',
    alias: 'widget.checkcolumn',

    /**
     * @cfg {Boolean} [stopSelection=true]
     * Prevent grid selection upon mousedown.
     */
    stopSelection: true,

    tdCls: Ext.baseCSSPrefix + 'grid-cell-checkcolumn',

    getStore: function() {
    	return this.store;//this.up('gridpanel').getStore();
    },
    
    constructor: function(config) {
    	var me = this;
    	if(config.columnHeaderCheckbox)
        {
    		if (config.store != null) {
    			this.updateComponentWithStore(config.store);
    		}
        }
        this.addEvents(
            /**
             * @event beforecheckchange
             * Fires when before checked state of a row changes.
             * The change may be vetoed by returning `false` from a listener.
             * @param {Ext.ux.CheckColumn} this CheckColumn
             * @param {Number} rowIndex The row index
             * @param {Boolean} checked True if the box is to be checked
             */
            'beforecheckchange',
            /**
             * @event checkchange
             * Fires when the checked state of a row changes
             * @param {Ext.ux.CheckColumn} this CheckColumn
             * @param {Number} rowIndex The row index
             * @param {Boolean} checked True if the box is now checked
             */
            'checkchange'
        );
        this.callParent(arguments);
    },
    
    updateComponentWithStore: function(store) {
    	this.store = store;
        this.store.on('datachanged', 
        	this.updateColumnHeaderCheckbox,
        	this
        );
        this.store.on('update', 
        		this.updateColumnHeaderCheckbox,
        		this
        );
		this.setText(this.getHeaderCheckboxImage());
    },   
    
    updateColumnHeaderCheckbox: function(){
        var image = this.getHeaderCheckboxImage();
        this.setText(image);
    },
    
    getHeaderCheckboxImage: function(){

        var allTrue = this.getStoreIsAllChecked();

        var cssPrefix = Ext.baseCSSPrefix,
            cls = [cssPrefix + 'grid-checkheader'];

        if (allTrue) {
            cls.push(cssPrefix + 'grid-checkheader-checked');
        }
        return '<div class="' + cls.join(' ') + '">&#160;</div>'
    },
    
    getStoreIsAllChecked: function() {
        var allTrue = true;
        var dataIndex = this.dataIndex;
        var store = this.getStore();
        store.each(function(record){
            if(!record.get(dataIndex))
                allTrue = false;
        });
        return allTrue;
    },
    
    toggleSortState: function(){
        var me = this;
        if (me.columnHeaderCheckbox)
        {
            var store = this.getStore();
            var isAllChecked = this.getStoreIsAllChecked();
            store.each(function(record){
            	var value = me.getCheckedValue(record, !isAllChecked);
                record.set(me.dataIndex, value);
                record.commit();
            });
        	me.setText(me.getHeaderCheckboxImage());
        }
        else {
        	me.callParent(arguments);
        }
    },
    
    getCheckedValue: function(record, value) {
    	return value;
    },

    /**
     * @private
     * Process and refire events routed from the GridView's processEvent method.
     */
    processEvent: function(type, view, cell, recordIndex, cellIndex, e, record, row) {
        var me = this,
            key = type === 'keydown' && e.getKey(),
            mousedown = type == 'mousedown';

        if (mousedown || (key == e.ENTER || key == e.SPACE)) {
        	if (me.disabled == true || me.columnDisabled == true) {
        		return false;
        	}
        	if (me.allowValueChange && !me.allowValueChange(record)) {
        		return false;
        	}
            var dataIndex = me.dataIndex,
                checked = !record.get(dataIndex);

            // Allow apps to hook beforecheckchange
            if (me.fireEvent('beforecheckchange', me, recordIndex, cellIndex, e, record, row, checked) !== false) {
                record.set(dataIndex, checked);
                me.fireEvent('checkchange', me, recordIndex, cellIndex, e, record, row, checked);

                // Mousedown on the now nonexistent cell causes the view to blur, so stop it continuing.
                if (mousedown) {
                    e.stopEvent();
                }

                // Selection will not proceed after this because of the DOM update caused by the record modification
                // Invoke the SelectionModel unless configured not to do so
                if (!me.stopSelection) {
                    view.selModel.selectByPosition({
                        row: recordIndex,
                        column: cellIndex
                    });
                }

                // Prevent the view from propagating the event to the selection model - we have done that job.
                return false;
            } else {
                // Prevent the view from propagating the event to the selection model if configured to do so.
                return !me.stopSelection;
            }
        } else {
            return me.callParent(arguments);
        }
    },

    // Note: class names are not placed on the prototype bc renderer scope
    // is not in the header.
    renderer : function(value){
        var cssPrefix = Ext.baseCSSPrefix,
            cls = [cssPrefix + 'grid-checkheader'];

        if (value) {
            cls.push(cssPrefix + 'grid-checkheader-checked');
        }
        return '<div class="' + cls.join(' ') + '">&#160;</div>';
    }
});
