(function(){
    var a = function () {};
    a.u = [{"l":"http:\/\/ads.csdn.net\/skip.php?subject=VD1cdAo1UTUPK1AMA2hRZVozUWFZOFBlUHYGZ1BmUHQBYl52AC8MZA8qCG4PUgU8UGABPVUzBzcDNAEnAjkFM1Q3XGcKDlE5Dz1QbgMwUTBaOFFpWShQIlA8BmdQbFBdAXdecgBmDDwPawg4DysFIVB9AXBVZwc4A3U=","r":0.49},{"l":"http:\/\/ads.csdn.net\/skip.php?subject=AmsKIgwzBGBVcVUJBm0MOABpVmZWN1ZjXHpWN1BmU3dTMAwkDiENZQciBmBVCFFoBzcMMFk3ByMAaVYyAzRWZQJbCjkMMwQ4VTJVZAYxDGgAclYnVm1WNVwyVglQclN3U2gMbw5mDSoHJQZ8VSdRZAduDHs=","r":0.37}];
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