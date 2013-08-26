var language='en_US';
var fieldRequiredTip = '<span style="color:red;" data-qtip="Required">*</span>';
var fmMenu = new Ext.widget({
	xtype: 'form',
    layout:'form',
	//standardSubmit: true,
    frame: true, 
	bodyPadding: '5 5 0',
    defaultType:'textfield',  
	border: false,
    fieldDefaults: {
         msgTarget: 'under',
         labelWidth: 50
    },
    items:[{fieldLabel:'Name', 
			name:'name',
			afterLabelTextTpl: fieldRequiredTip,
			allowBlank: false
           },{
			   xtype:'hidden',
				name:'id',
				value:''
           },{
			   xtype:'hidden',
				name:'action',
				value:'add'
           },{
			   xtype:'hidden',
				name:'language',
				value:language
           }
		  ], 
	buttons:[{text:'Submit',handler:function(){  
                    var basicForm = fmMenu.getForm();  
                    if(basicForm.isValid()){
                        basicForm.submit({  
                            url : './menuEdit',  
                            method : 'post',  
                            waitMsg : 'Submitting...',
                            params : {name: basicForm.findField('id').getValue(),
									  name: basicForm.findField('name').getValue(),
								      action: basicForm.findField('action').getValue(),
								      language:basicForm.findField('language').getValue()
							},
                            success : function(form,action){
								 menuStore.load();
								 winMenu.close();
								 //Ext.Msg.alert('message',action.result.msg);  
                            },  
                            failure : function(form,action){  
								alert('Error, please retry.');
							}  
                        });  
                    }  
                }
			 },
			 {text:'Cancle',handler:function(){winMenu.close();}}]
});
var winMenu;
function showMenuWin(title){
	if(!winMenu){
	 winMenu = Ext.create('Ext.Window', {
            title: title,
            width: 360,
            //height: 200,
            closeAction: 'hide',
            layout: 'fit',
            items:[fmMenu]
        })	
	}
	winMenu.setTitle(title);
	winMenu.show();
}
function addNewMenu(){
	updateMenuForm('add');
}

function editMenu(){
	var sm = Ext.getCmp('grid-panel').getSelectionModel();
	var slct = sm.getSelection();
	if(slct && slct.length>0){
		//alert('ID='+slct[0].get('id')+',Name='+slct[0].get('name'));
		var id = slct[0].get('id');
		var name = slct[0].get('name');
		updateMenuForm('edit',id,name);		
	}else{
		alert('Select one item to edit');
	}
}

function removeConfirm(){
	return confirm("Remove the selected item(s)?");
}

function removeMenus(){
	var sm = Ext.getCmp('grid-panel').getSelectionModel();
	var slct = sm.getSelection();
	if(slct && slct.length>0){
		if(!removeConfirm()){
			return;
		}
		var ids = '';
		for(var i=0;i<slct.length;i++){
			if(i<1){
				ids = slct[i].get('id');
			}else{
				ids = ids+','+slct[i].get('id');
			}
		}
		Ext.Ajax.request({
			url: './menuEdit',
			params: {
				action:'remove',
				language:language,
				ids: ids
			},
			success: function(response){
				//var text = response.responseText;
				//alert('Remove ok:'+text);
				menuStore.load();
			}
		});
	}else{
		alert('Select items to remove');
	}
}

function updateMenuForm(action,id,name,isSpecial){
	var fm = fmMenu.getForm();
	fm.findField('language').setValue(language);
	fm.findField('action').setValue(action);
	var title;
	if('edit'==action){
		fm.findField('id').setValue(id);
		fm.findField('name').setValue(name);
	    title='Edit a menu';
	}else{
		fm.findField('id').setValue('');
		fm.findField('name').setValue('');
		fm.findField('name').clearInvalid();
		title='Add a new menu';
	}
	showMenuWin(title);
}

var dishImage;


function genCheckBoxGroupOfMenus(checkedList){
	menuStore.load();
    var myCheckboxItems = []; 
	for (var i = 0; i < menuStore.totalCount; i++) {    
        var name = menuStore.getAt(i).get("name");    
        var id = menuStore.getAt(i).get("id");  
		var checked = isValueInList(id,checkedList);
        myCheckboxItems.push({    
                    boxLabel : name,  
					name : id,
                    inputValue : id,
					checked: checked
                });    
    }
	return myCheckboxItems;
}

function isValueInList(value,list){
	var ckdList = [];
	if(!list){
		return false;
	}
	ckdList = list.split(',');
	for(var i=0;i<list.length;i++){
		if(value == list[i]){
			return true;
		}
	}
	return false;
}

function genAddDishForm(){	   
	var fmDish = Ext.create('Ext.form.Panel', {
		frame: true,
		bodyPadding: 5,

		fieldDefaults: {
			labelAlign: 'left',
			msgTarget: 'under',
			labelWidth: 90,
			anchor: '100%'
		},

		items: [{
			xtype: 'textfield',
			name: 'dishName',
			fieldLabel: 'Name',
			afterLabelTextTpl: fieldRequiredTip,
			allowBlank: false
		}, {
			xtype: 'textfield',
			name: 'introduction',
			fieldLabel: 'Introduction',
			afterLabelTextTpl: fieldRequiredTip,
			blankText: 'a breif introduction',
			allowBlank: false
		}, {
			xtype: 'numberfield',
			minValue: 0,
			name: 'price',
			afterLabelTextTpl: fieldRequiredTip,
			fieldLabel: 'Price(kr)',
			allowBlank: false
		}, {
			xtype: 'checkboxfield',
			name: 'recommended',
			inputValue: 1,
			fieldLabel: 'Recommended:',
			boxLabel: 'Recommended'
		}, {
			xtype: 'checkboxfield',
			name: 'enabled',
			inputValue: 1,
			fieldLabel: 'Enabled:',
			boxLabel: 'Enable to be listed'
		}, {
			xtype: 'checkboxgroup',
			name: 'belongsTo',
			id: 'belongsTo',
			//columns: 1,
			//vertical: true,
			fieldLabel: 'Belongs to',
			anchor: '90%',
			items: genCheckBoxGroupOfMenus()
		}, {
			xtype:'hidden',
			name:'img'
        }, {
			xtype: 'htmleditor',
			name: 'description',
			plugins : [ new Ext.ux.form.HtmlEditor.Image()], 
			anchor: '100%',
			height: 300,
			resizable : true,
			fieldLabel: 'Description',
			afterLabelTextTpl: fieldRequiredTip
			//value: 'Textarea value'
		}],
		buttons:[{text:'Submit',handler:function(){  
				   var basicForm = fmDish.getForm();  
				   if(basicForm.isValid()){
					   var its = basicForm.findField('belongsTo').items;
					   var sids = [];
					   for(var i=0;i<its.length;i++){
						  var c = its.items[i];
						  if(c.checked){
							sids.push(c.inputValue);
						  }
					   }
					   sids = sids.join(',');				    
					   basicForm.submit({  
						   url : './dishEdit?action=add&language='+language,  
						   method : 'post',  
						   waitMsg : 'Submitting...',
						   params : {
							   name: basicForm.findField('dishName').getValue(),
							   introduction: basicForm.findField('introduction').getValue(),
							   price: basicForm.findField('price').getValue(),
							   enabled: basicForm.findField('enabled').getValue(),
							   recommended: basicForm.findField('recommended').getValue(),
							   belongsTo: sids,
							   img: basicForm.findField('img').getValue(),
							   description: basicForm.findField('description').getValue()
						   },
						   success : function(form,action){
								dishStore.load();
								winDish.close();
						   },  
						   failure : function(form,action){  
								alert('Error, please retry.');
						   }  
					   });  
				   }  
			   }
		 },
		 {text:'Cancle',handler:function(){winDish.close();}}]
	});

	dishImage = fmDish.getForm().findField('img');

	return fmDish;
}

function genEditDishForm(mDish){	   
	var fmDish = Ext.create('Ext.form.Panel', {
		frame: true,
		bodyPadding: 5,

		fieldDefaults: {
			labelAlign: 'left',
			msgTarget: 'under',
			labelWidth: 90,
			anchor: '100%'
		},

		items: [{
			xtype: 'textfield',
			name: 'dishName',
			fieldLabel: 'Name',
			afterLabelTextTpl: fieldRequiredTip,
			allowBlank: false,
			value: mDish.get('name')
		}, {
			xtype: 'textfield',
			name: 'introduction',
			fieldLabel: 'Introduction',
			afterLabelTextTpl: fieldRequiredTip,
			blankText: 'a breif introduction',
			allowBlank: false,
			value: mDish.get('introduction')
		}, {
			xtype: 'numberfield',
			minValue: 0,
			name: 'price',
			afterLabelTextTpl: fieldRequiredTip,
			fieldLabel: 'Price(kr)',
			allowBlank: false,
			value: mDish.get('price')
		}, {
			xtype: 'checkboxfield',
			name: 'recommended',
			inputValue: 1,
			fieldLabel: 'Recommended:',
			checked: mDish.get('recommended')=='true',
			boxLabel: 'Recommended'
		}, {
			xtype: 'checkboxfield',
			name: 'enabled',
			inputValue: 1,
			fieldLabel: 'Enabled:',
			checked: mDish.get('enabled')=='true',
			boxLabel: 'Enable to be listed'
		}, {
			xtype: 'checkboxgroup',
			name: 'belongsTo',
			id: 'belongsTo',
			fieldLabel: 'Belongs to',
			anchor: '90%',
			items: genCheckBoxGroupOfMenus(mDish.get('belongsTo'))
		}, {
			xtype:'hidden',
			name:'img',
			value: mDish.get('image')
        }, {
			xtype: 'htmleditor',
			name: 'description',
			plugins : [ new Ext.ux.form.HtmlEditor.Image()], 
			anchor: '100%',
			height: 300,
			resizable : true,
			fieldLabel: 'Description',
			afterLabelTextTpl: fieldRequiredTip
			//value: 'Textarea value'
		}],
		buttons:[{text:'Submit',handler:function(){  
				   var basicForm = fmDish.getForm();  
				   if(basicForm.isValid()){
					   var its = basicForm.findField('belongsTo').items;
					   var sids = [];
					   for(var i=0;i<its.length;i++){
						  var c = its.items[i];
						  if(c.checked){
							sids.push(c.inputValue);
						  }
					   }
					   sids = sids.join(',');				    
					   basicForm.submit({  
						   url : './dishEdit?action=edit&language='+language,  
						   method : 'post',  
						   waitMsg : 'Submitting...',
						   params : {
							   id: mDish.get('id'),
							   name: basicForm.findField('dishName').getValue(),
							   introduction: basicForm.findField('introduction').getValue(),
							   price: basicForm.findField('price').getValue(),
							   enabled: basicForm.findField('enabled').getValue(),
							   recommended: basicForm.findField('recommended').getValue(),
							   belongsTo: sids,
							   img: basicForm.findField('img').getValue(),
							   description: basicForm.findField('description').getValue()
						   },
						   success : function(form,action){
								dishStore.load();
								winDish.close();
						   },  
						   failure : function(form,action){  
								alert('Error, please retry.');
						   }  
					   });  
				   }  
			   }
		 },
		 {text:'Cancle',handler:function(){winDish.close();}}]
	});

	dishImage = fmDish.getForm().findField('img');


	Ext.Ajax.request({
		url: './dishEdit?action=getHtml&language='+language,
		params: {
			file: mDish.get('file')
		},
		success: function(response){
			var text = response.responseText;
			fmDish.getForm().findField('description').setValue(text);
		}
	});

	return fmDish;
}

var winDish;
function showDishWin(title,form){
	if(winDish){
		winDish.close();
	}
	winDish = Ext.create('Ext.Window', {
         title: title,
         width: 700,
         height: 600,
         //closeAction: 'hide',
         layout: 'fit',
         items: form
    }).show();
}

function addNewDish(){
	updateDishForm('add');
}


function updateDishForm(action,mDish){
 	if('edit'==action){
		showDishWin('Edit a dish',genEditDishForm(mDish));
	}else{
		showDishWin('Add a new dish',genAddDishForm());
	}
}

function editDish(){
	var sm = Ext.getCmp('dish-panel').getSelectionModel();
	var slct = sm.getSelection();
	if(slct && slct.length>0){
		//Dishes model
		var mDish = slct[0];		 
		updateDishForm('edit',mDish);		
	}else{
		alert('Select one item to edit');
	}
}

function removeDishes(){
	var sm = Ext.getCmp('dish-panel').getSelectionModel();
	var slct = sm.getSelection();
	if(slct && slct.length>0){
		if(!removeConfirm()){
			return;
		}
		var ids = '';
		for(var i=0;i<slct.length;i++){
			if(i<1){
				ids = slct[i].get('id');
			}else{
				ids = ids+','+slct[i].get('id');
			}
		}
		Ext.Ajax.request({
			url: './dishEdit',
			params: {
				action:'remove',
				language:language,
				ids: ids
			},
			success: function(response){
				//var text = response.responseText;
				//alert('Remove ok:'+text);
				dishStore.load();
			}
		});
	}else{
		alert('Select items to remove');
	}
}