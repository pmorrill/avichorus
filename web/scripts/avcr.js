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

var g_player = 'pl_twrap'; // div to use for player
var g_media = null; // file to play

$(function() {
	$('#spectrogram-wrap').css('cursor', 'wait'); 
	if ( g_media != null ) init_player6(true,g_media,g_player,true,null,null,avcr_jwp_cb_spectro_play);
    $('#media_position').html("0.0 of "+g_pc_length.toFixed(1)+'s');
    $('#spectrogram-display').scroll(avcr_scroll);
    $('#spectrogram-wrap').css('cursor', 'default'); 
});
