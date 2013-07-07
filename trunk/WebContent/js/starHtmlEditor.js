
function isImageObject(file){
	var fileName = /^.*\.(jpg|jpeg|png|gif)$/i;
    return fileName.test(file);
}

function isAudioObject(file){
	var fileName = /^.*\.(mp3)$/i;
    return fileName.test(file);
}

function isVideoObject(file){
	var fileName = /^.*\.(mp4)$/i;
    return fileName.test(file);
}

Ext.apply(Ext.form.field.VTypes, {
    
    //  vtype validation function
    file : function(val, field) {
        var fileName = /^.*\.(jpg|png|gif|mp3|mp4)$/i;
        return fileName.test(val);
    },
    // vtype Text property to display error Text
    // when the validation function returns false
    fileText : "File must be jpg,png,gif,mp3 or mp4",
    // vtype Mask property for keystroke filter mask
    fileMask : /[a-z_\.]/i 
});

Ext.define('Ext.ux.form.HtmlEditor.Image', {
    extend: 'Ext.util.Observable',

    // Image language text
    langTitle   : 'Insert Image',

    urlSizeVars : ['width','height'],

    basePath    : 'data',

    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
        this.cmp.on('initialize', this.onInit, this, {delay:100, single: true});
    },

    onEditorMouseUp : function(e){
        Ext.get(e.getTarget()).select('img').each(function(el){
            var w = el.getAttribute('width'), h = el.getAttribute('height'), src = el.getAttribute('src')+' ';
            src = src.replace(new RegExp(this.urlSizeVars[0]+'=[0-9]{1,5}([&| ])'), this.urlSizeVars[0]+'='+w+'$1');
            src = src.replace(new RegExp(this.urlSizeVars[1]+'=[0-9]{1,5}([&| ])'), this.urlSizeVars[1]+'='+h+'$1');
            el.set({src:src.replace(/\s+$/,"")});
        }, this);

    },

    onInit: function(){
        Ext.EventManager.on(this.cmp.getDoc(), {
            mouseup     : this.onEditorMouseUp,
            buffer      : 100,
            scope       : this
        });
    },

    onRender: function() {
        var btn = this.cmp.getToolbar().add({
            iconCls     : 'x-edit-image',
            handler     : this.selectImage,
            scope       : this,
            tooltip     : {
                title : this.langTitle
            },
            overflowText: this.langTitle
        });
    },



    selectImage: function(){
		var editor = this;
        var imgform = new Ext.FormPanel({
            region : 'center',
            labelWidth : 30,
            frame : true,
            bodyStyle : 'padding:5px 5px 0',
            //autoScroll : true,
            border : false,
            fileUpload : true,
            items : [{
                        xtype : 'filefield',
                        fieldLabel : 'File',
						labelWidth: 20,
                        name : 'userfile',
						msgTarget: 'under',
                        allowBlank : false,
						vtype: 'file',
					    buttonText: 'Browse...',
                        anchor : '100%'
                    }],
            buttons : [{
                text : 'Upload',
                type : 'submit',
                handler : function() {
                    if (!imgform.form.isValid()) {return;}
                    imgform.form.submit({
                        waitMsg : 'Uploading...',
                        url : './upload',
						params : {language:language },
                        success : function(form, action) {
                           /* var element = document.createElement("img");
                            element.src = action.result.fileName;
                            if (Ext.isIE) {
                                editor.insertAtCursor(element.outerHTML);
                            } else {
                                var selection = editor.win.getSelection();
                                if (!selection.isCollapsed) {
                                    selection.deleteFromDocument();
                                }
                                selection.getRangeAt(0).insertNode(element);
                            }
							*/
							editor.insertImage(action.result.fileName);
                            win.hide();
                        },
                        failure : function(form, action) {
                            form.reset();
                            if (action.failureType == Ext.form.Action.SERVER_INVALID)
                                Ext.MessageBox.alert('Warn', action.result.errors.msg);
                        }
                    });
                }
            }, {
                text : 'Close',
                type : 'submit',
                handler : function() {
                    win.close(this);
                }
            }]
        })

        var win = new Ext.Window({
                    title : "Upload Objects(image,mp3 or mp4)",
                    width : 400,
                    height : 120,
                    modal : true,
                    border : false,
                    layout : "fit",
                    items : imgform
                });
        win.show();	
	},

    insertImage: function(fileObj) {
		var file = this.basePath +'/'+language+'/dishes/';
		if(isImageObject(fileObj)){
			file = file+'images/'+fileObj;
			dishImage.setValue(fileObj);
			this.cmp.insertAtCursor('<img src="'+file+'" title="'+fileObj+'" alt="'+fileObj+'">');
		}else if(isAudioObject(fileObj)){
			file = file+'audios/'+fileObj;
			this.cmp.insertAtCursor('<audio src="'+file+'" title="'+fileObj+'" alt="'+fileObj+'" controls="controls">Your browser does not support the audio tag.</audio>');
		}else if(isVideoObject(fileObj)){
			file = file+'videos/'+fileObj;
			this.cmp.insertAtCursor('<video src="'+file+'" title="'+fileObj+'" alt="'+fileObj+'" controls="controls">Your browser does not support the video tag.</video>');
		}
    }
});