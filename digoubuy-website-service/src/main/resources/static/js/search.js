$(function () {
    search();
    // $('#button').click(
    //     search()
    // );
    function search() {
        var url = location.href;
        var keyword = decodeURI(url.substring(url.indexOf("=")+1,url.length));
        $.ajax({
            type:"POST",
            url:"http://localhost:8666/product/search",
            data:JSON.stringify({
                "searchContent" : keyword
            }),
            contentType:"application/json",
            dataType:"json",
            success:function (data) {
                if (data.code != 200) {
                    search();
                } else {

                    var product = data.data;
                    for (var i = 0; i < product.length; i++) {
                        $('#pro-list').append(
                            "<li>\n" +
                            "    <div class=\"pro-panels \"><input type=\"hidden\" id=\"itemdata" + i + "\"" +
                            "         itemid=\""+product[i].id+"\" itemname=\"" + product[i].spuName +"\"><a\n" +
                            "         href=\"\" target=\"_blank\"\n" +
                            "                                onclick=\"\">\n" +
                            "                            <p class=\"p-img\"><img src=\"" + product[i].titleImgUrl + "\"\n" +
                            "                                                  alt=\"" + product[i].spuName +"\"></p>\n" +
                            "                            <p class=\"p-name\" title=\""+product[i].titleName+"\">\n" +
                            "                                "+product[i].titleName+"</p>\n" +
                            "                            <p class=\"p-price\"><b>¥"+ product[i].titlePrice+"<span>多配置可选</span></p></a></div>\n" +
                            "                    </li>"
                        );
                    }

                }
            }
        });
    }
});