/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function init_dlg(ops) {
	$(document).bind('mousemove',function(e) {
		if ( dragging ) {
			var dx = e.pageX - dragging.startP[0]; var dy = e.pageY - dragging.startP[1];
			dragging.dlg.offset({left:dragging.startD.left + dx,top:dragging.startD.top + dy});
		}
	});
	if ( ops.autocancel != 'no' ) $(document).keyup(function(e) { if ( e.keyCode == 27 ) close_ajax_dlg(); });
	$(document).data('initialized',1);
}

function close_ajax_dlg() { $('#ajax_dlg').trigger('close'); $('#ajax_mask').fadeOut(500); $('#ajax_dlg').fadeOut('slow',function() {$('#ajax_dlg').remove(); }); }

function display_ajax_form(html,ops,cl) {
  if ( $(document).data('initialized') != 1 ) init_dlg(ops);
  if ( !ops ) ops = {};
  $('body').append('<div id="ajax_dlg" tabindex="0" class="'+cl+'"><div class="title"><div>'+ops.title+'</div></div><div style="clear: both"> </div><div id="ajax_content">'+html+'</div></div>');
  if ( ops.onclose ) $('#ajax_dlg').on('close',ops.onclose);
  $('.title').bind('mousedown',function(e) {$('.title').css('cursor','move'); dragging = {dlg:$('#ajax_dlg'),startP:[e.pageX,e.pageY],startD:$('#ajax_dlg').offset()};});
  $('.title').bind('mouseup',function(e) {$('.title').css('cursor','default'); dragging = null});
  $('.close_btn').click(function() {close_ajax_dlg()});
  
  $('#ajax_dlg').css('left',($(window).width() - $('#ajax_dlg').width()) / 2);
  $('#ajax_dlg').css('top',$(window).scrollTop() + ($(window).height() - $('#ajax_dlg').height()) / 2);
  if ( ops.modal > 0 ) mask_screen(ops.modal,ops.autocancel);
  $('#ajax_dlg').fadeIn('slow',function() { $(ops.focus).focus(); });

 // position_ajax_dlg(ops);
}

function mask_screen(op,autocancel) {
	if ( !$('#ajax_mask').length ) $('body').append('<div id="ajax_mask"></div>');
	if ( !op ) op = 0.4;
	$('#ajax_mask').css({'width':$('body').width(),'height':$(document).height(),'top': '0px','left': '0px'});
	if ( autocancel != 'no' ) $('#ajax_mask').click(function() { close_ajax_dlg(); });
	$('#ajax_mask').fadeTo('slow',op);
}

/**
 * This will post the tag form tot he server and register the tag and change
 * 
 * @param {type} id
 * @returns {undefined}
 */
function saveTag(id) {
  $.post('/avcr-abmi/demo/tag',
          $('#active-notes').serialize(),
          function (json) {
            close_ajax_dlg();
            $('#' + json.id).removeClass('recon_1 recon_2 recon_3 recon_0 recon_4 recon_hide').addClass(json.color);
            $.get('/avcr-abmi/demo/refresh-tags', function (html) {
              $('#avcr_sp_list').html(html);
            });
    },'json');
  
}
