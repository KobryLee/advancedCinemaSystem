$(document).ready(function() {

    getVipInfo();

    function getVipInfo() {
        getRequest(
            '/vip/getVIPInfo',
            function (res) {
                var vipInfoList = res.content;
                renderVipInfo(vipInfoList);
            },
            function (error) {
                alert(JSON.stringify(error));
            }
        );
    }
    
    function renderVipInfo(vipInfoList) {
        $(".content-vipInfo").empty();
        var vipInfoDomStr = "";

        var idx = 0;
        vipInfoList.forEach(function (vipInfo) {
        	vipInfoDomStr +=
                "<li class='vipInfo-container'>" +
                "    <div class='vipInfo-card card'>" +
                "       <div class='vipInfo-line'>" +
                "           <span class='title'>" + vipInfo.name + "</span>" +
                "       </div>" +
                "       <div class='vipInfo-line'>" +
                "           <span>价格：" + 
                "           <span class='primary-text'>" + vipInfo.price + "</span>" +
                "       </div>" +
                "       <div class='vipInfo-line'>" +
                "           <span>优惠：满  " +
                "           <span class='primary-text'>" + vipInfo.discount_req + "</span>" +
                "           <span> 减  </span>" +
                "           <span class='primary-text'>" + vipInfo.discount_res + "</span>" +
                "           </span>" +
                "           <span class='change-btn'>" +
    			"				<button type='button' id='btn" + idx + "' class='btn btn-primary' data-backdrop='static' data-toggle='modal' data-target='#vipInfoModal'> 修改 </button>" +
    			"			</span>" +
                "       </div>" +
                "    </div>" +
                "</li>";
        	
        	idx++;
        });
        $(".content-vipInfo").append(vipInfoDomStr);
    }

    $("#vipInfo-form-btn").click(function () {
        var form = {
            name: $("#vipInfo-name-input").val(),
            price: $("#vipInfo-price-input").val(),
            discount_req: $("#vipInfo-discount_req-input").val(),
            discount_res: $("#vipInfo-discount_res-input").val(),
        };

         postRequest(
             '/vip/publish',
             form,
             function (res) {
                 if(res.success){
                     getVipInfo();
                     $("#vipInfoModal").modal('hide');
                 } else {
                     alert(res.message);
                 }
             },
             function (error) {
                 alert(JSON.stringify(error));
             }
         );
     });
    	
});
