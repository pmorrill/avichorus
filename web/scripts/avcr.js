/**
 * Javascript to support the Point Count Recording analysis servlets and webpages
 * 
 * @type type
 */
var stopIcon = {
    path: google.maps.SymbolPath.CIRCLE,
    fillColor: '#FF6666',
    fillOpacity: .9,
    scale: 6.5,
    strokeColor: '#000000',
    strokeWeight: 1
};
var iWindow = null;
var g_map = null;
var g_speed = 0;
var g_x = 0;
var g_mState = 0;
var g_no_seek = false;
var g_endAt = 0;
var g_pc_length = 0;

/**
 * Use Google maps to display a map of the recordings in the project
 * 
 * @param {type} data JSON encoded data structure
 * @returns {undefined}
 */
function map(data) {
  $div = document.getElementById('recordingMap');
  if ( $div.length == 0 ) return;
  var mt = google.maps.MapTypeId.TERRAIN;
  var long_lat = new google.maps.LatLng(data.centery,data.centerx);
  var myOptions = {
    zoom: data.zoom,
    center: long_lat,
    disableDefaultUI: false,
    mapTypeId: mt
  };
  var g_map = new google.maps.Map($div, myOptions);
  var g_markers = [];
  for (var i = 0; i < data.markers.length; i++) {
    mIcon = stopIcon;
    myLatLng = new google.maps.LatLng(data.markers[i]["latitude"], data.markers[i]["longitude"]);
    var dot = new google.maps.Marker({
      position: myLatLng,
      icon: mIcon,
      map: data.cluster ? null : g_map,
      title: data.markers[i]["recording"],
      shadow: null
    });

    g_markers[i] = dot;
    var info = "<div class=\"marker-info\"><strong>" + data.markers[i].longitude + ", " + data.markers[i].latitude + "<br />" +
            data.markers[i].habitat + "</strong><br /><br />" + data.markers[i].siteName + " (" + data.markers[i].province + ")<br />"
            + data.markers[i].recording;
    info += '&nbsp;&nbsp;<a href="/avcr-abmi/demo/play?id=' + data.markers[i].recordingId + '">Analyse</a><br /></div>'
    g_markers[i].info = info;
    g_markers[i].addListener("click", function () {
      if (iWindow) iWindow.close();
      iWindow = new google.maps.InfoWindow({content: this.info});
      iWindow.open(g_map, this);
    });
  }

  g_map.addListener("click",function() { if ( iWindow ) { iWindow.close(); iWindow = null; } });
}

function init_player6(silent,play_url,d_id,bAutoStart,cb_scroll,cb_onseek,cb_onplay) {
	gPlayerId = d_id;
	if ( !play_url ) { play_url = 'avichorus/recordings/silence.mp3'; autoStart = 0; }
	jwplayer(d_id).setup({
        	file: play_url,
        	height: 24,width: 310,
		title: '',autostart: false,
		primary: 'flash',repeat: false
	});
	jwplayer(d_id).setVolume(50);
	if ( !silent && bAutoStart ) jwplayer(d_id).play(true);
	if ( cb_onseek ) jwplayer(d_id).onSeek(cb_onseek);
	if ( cb_onplay ) jwplayer(d_id).onPlay(cb_onplay);
	if ( cb_scroll ) jwplayer(d_id).onTime(cb_scroll);
	gPlayerVersion = 6;
}

function avcr_jwp_pause() {
	$('#spectrogram-display').removeClass('noscroll'); jwplayer(g_player).pause(true);
	g_endAt = 0;
}
function avcr_jwp_play() {
	$('#spectrogram-display').addClass('noscroll'); jwplayer(g_player).play(true);
}

function avcr_restart() {
  jwplayer().seek(0);
  $('#spectrogram-display').addClass('noscroll');
}

function avcr_scroll(e) {
	if ( jwplayer(g_player).getState() == 'PLAYING' ) return;
	var t = e.currentTarget;
	var ss = t.scrollLeft / (t.scrollWidth - t.clientWidth) * g_pc_length;
	$('#media_position').html(ss.toFixed(1)+' of '+g_pc_length.toFixed(1)+' s');
	g_no_seek = 1;
	jwplayer(g_player).seek(ss); jwplayer(g_player).pause(true);
	setTimeout(function() { g_no_seek = 0; },500);
}

/* call back functions */
function avcr_jwp_cb_spectro_scroll(p) {
	var r = Math.round(p.position * g_speed);
	if ( r != g_x ) {
		g_x = r;
		if ( !g_no_seek ) $('#spectrogram-display').scrollLeft(r);
//		if ( g_endAt && p.position>g_endAt ) avcr_jwp_pause();
	}
	$('#media_position').html(p.position.toFixed(1)+' of '+g_pc_length.toFixed(1)+' s');
}
function avcr_jwp_cb_spectro_play(p) { $('#new_sel').remove(); g_mState = 0; }

function avcr_jwp_cb_spectro_seek(p) { }


/**
 * Toggle the play action: start or pause
 * 
 * @param {type} st true to start player
 * @returns {undefined}
 */
function avcr_play(st) {
	//var state = st ? 'PLAYING' : 'PAUSED';
    state = jwplayer(g_player).getState();
	if ( state == 'PLAYING' ) avcr_jwp_pause();
    else avcr_jwp_play();
}

function avcr_img_cancel_select(e,d) {
  $('#new_sel').remove(); $('#media_msg').html(''); g_mState = 0;
  if ( g_was_playing ) jwplayer(g_player).play(true);
  g_was_playing = 0;
}

function avcr_img_begin_select(e,d) {
    // deprecated ??
	if ( g_no_tagging ) return;
    
    // temp check on new_sel length, must be removed?
	if ( $('#new_sel').length > 0 || e.which != 1 || g_mState != 0 ) { return avcr_img_cancel_select(e,d); }
	g_mState = 1;
	if ( jwplayer(g_player).getState() == 'PLAYING' ) { jwplayer(g_player).pause(true); g_was_playing = 1; }
	var dp = $(d).offset();
	t = e.pageY-dp.top;
	l = e.pageX-dp.left;
	$(d).append('<div class="sel_box new_sel_box" id="new_sel" style="top: '+t+'px; left: '+l+'px; width: 0px; height: 0px"></div>');
	$('#new_sel').data('start',[t,l]).data('offset',$('#new_sel').offset());
}

function avcr_img_move_select(e,d) {
	if ( e.which != 1 || g_mState != 1 ) { return; }
	var ns = $('#new_sel');
	if ( !ns.length ) return;
	var t = ns.data('start')[0]; var l = ns.data('start')[1];
	var of = ns.data('offset');
	/* use the original stored starting offset to get height and width */
	h = e.pageY - of.top;
	w = e.pageX - of.left;
	if ( h < 0 ) { t = t + h; h = -h; }
	if ( w < 0 ) { l = l + w; w = -w; }
	ns.css({top:t,left:l,height:h,width:w});
}

function avcr_img_end_select(e,d) {
	if ( e.which != 1 || g_mState != 1 ) return;
	var ns = $('#new_sel');
	if ( ns.width() < 15 || ns.height() < 15 ) return avcr_img_cancel_select(e,d);
    g_mState = 0; // temp to stop dragging the box bigger

    /* show form in modal state */
	$.get('/avcr-abmi/demo/tag-form?rid='+g_pc_id,function(html) {
		display_ajax_form(html,{modal:0.4,title:'Tag a New Species',onclose:avcr_img_cancel_select,focus:'#active_notes .sp_code'},'avcr_notes'); 
		/* add data to the form */
		var nsp = $('#new_sel').position();
		c = nsp.left+';'+nsp.top;
		d = ns.width()+';'+ns.height();
		$('#ajax_dlg form .coords').val(c); $('#ajax_dlg form .box_size').val(d);
	});
}

function display_coords(obj) {
	return;

	if ( !obj || !obj.length ) { $('#media_msg').html(''); return; }
	dp = obj.offset();
	x1 = (dp.left - $('#spectrogram-images').offset().left) / g_speed;
	y1 = (dp.top - $('#spectrogram-images').offset().top) / g_range * 1000;
	x2 = x1 + obj.width() / g_speed;
	y2 = y1 + obj.height() / g_range * 1000;
	$('#media_msg').html(x1.toFixed(1)+','+y1.toFixed(1)+' -> '+x2.toFixed(1)+','+y2.toFixed(1));
}

/**
 * Display an existing tag
 * 
 * @param {type} id
 * @param {type} st
 * @returns {undefined}
 */
function edit_tag(id) {
    $.get('/avcr-abmi/demo/tag-form?rid='+g_pc_id+"&tagid="+id,function(html) {
		display_ajax_form(html,{modal:0.4,title:'Edit Species Tag',onclose:avcr_img_cancel_select,focus:'#active_notes .sp_code'},'avcr_notes'); 
    });
}

var g_player = 'pl_twrap'; // div to use for player
var g_media = null; // file to play
var g_no_tagging = false;
var g_was_playing = false;
var dragging = null;

$(function() {
	$('#spectrogram-wrap').css('cursor', 'wait'); 
	if ( g_media != null ) init_player6(true,g_media,g_player,true,avcr_jwp_cb_spectro_scroll,null,null);
	$('#spectrogram-images img').mousemove(function(e) {if ( e.which == 1 ) e.preventDefault();});
	$('#spectrogram-images img').mousedown(function(e) {if ( e.which == 1 ) e.preventDefault();});
	$('#spectrogram-images img').mouseup(function(e) {if ( e.which == 1 ) e.preventDefault();});
    $('#media_position').html("0.0 of "+g_pc_length.toFixed(1)+'s');
    
    $('#spectrogram-images').mousedown(function(e) { avcr_img_begin_select(e,this); });
	$('#spectrogram-images').mouseup(function(e) { avcr_img_end_select(e,this); });
	$('#spectrogram-images').mousemove(function(e) { avcr_img_move_select(e,this); });
	$('#spectrogram-images').mouseleave(function(e) { avcr_img_end_select(e,this); });
	$('.sel_box').mouseover(function(e) { display_coords($(e.currentTarget));});
	$('.sel_box').mouseout(function(e) { display_coords(null);});
	$('.sel_box').mousedown(function(e) {if ( e.which == 1 && e.ctrlKey ) { e.stopPropagation(); edit_tag($(this).attr('id')); }});
    
    $('#spectrogram-display').scroll(avcr_scroll);
    $('#spectrogram-wrap').css('cursor', 'default'); 
});
