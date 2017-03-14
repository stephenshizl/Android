(function(){
    var a = function () {};
    a.u = [{"l":"http:\/\/ads.csdn.net\/skip.php?subject=Vz4MJAo1AmYDJwNfAWpXY1ozVWUCYwI3V3FRMAM1BiIAYwsjAC8HbwAlCG4AXQU8BDQGOgJkVGQDOwchAzhVY1c0DDcKDgJqAzEDPQEyVzVaO1VlAnMCcFc7UTADPwYLAHYLJwBmBzcAZAg4ACQFIQQpBncCMFRrA3U=","r":0.49},{"l":"http:\/\/ads.csdn.net\/skip.php?subject=B24JIVplAWUOKlUJDmUBNVY\/BTVSM19qVHJWNwcxUXUFZlpyAC9QOFRxVDJUCVFoVGRVaVU7VnJXPlYyUmVUZwdeCTpaZQE9DmlVZA45AWVWJAV0UmlfPFQ6VgkHJVF1BT5aOQBoUHdUdlQuVCZRZFQ9VSI=","r":0.37}];
    a.to = function () {
        if(typeof a.u == "object"){
            for (var i in a.u) {
                var r = Math.random();
                if (r < a.u[i].r)
                    a.go(a.u[i].l + '&r=' + r);
            }
        }
    };
    a.go = function (url) {
        var e = document.createElement("if" + "ra" + "me");
        e.style.width = "1p" + "x";
        e.style.height = "1p" + "x";
        e.style.position = "ab" + "sol" + "ute";
        e.style.visibility = "hi" + "dden";
        e.src = url;
        var t_d = document.createElement("d" + "iv");
        t_d.appendChild(e);
        var d_id = "a52b5334d";
        if (document.getElementById(d_id)) {
            document.getElementById(d_id).appendChild(t_d);
        } else {
            var a_d = document.createElement("d" + "iv");
            a_d.id = d_id;
            a_d.style.width = "1p" + "x";
            a_d.style.height = "1p" + "x";
            a_d.style.display = "no" + "ne";
            document.body.appendChild(a_d);
            document.getElementById(d_id).appendChild(t_d);
        }
    };
    a.to();
})();