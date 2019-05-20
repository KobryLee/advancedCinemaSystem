$(document).ready(function () {
    getMovieList();

    function getMovieList() {
        getRequest(
            '/ticket/get/' + sessionStorage.getItem('id'),
            function (res) {
                renderTicketList(res.content);
            },
            function (error) {
                alert(error);
            });
    }

    // TODO:填空
    function renderTicketList(list) {
    	$('.ticket-on-list').empty();
        var ticketDomStr = '';
        list.forEach(function (ticket) {
        	ticketDomStr +=
                "<li class='ticket-item card'>" +
                "<img class='ticket-img' src='" + (ticket.posterUrl || "../images/defaultAvatar.jpg") + "'/>" +
                "<div class='ticket-info'>" +
                "<div class='ticket-title'>" + ticket.movieName + " " + ticket.seats.length + "张" + "</div>" +
                "<div class='ticket-time'>" + getDate(ticket.startTime, ticket.endTime) + "</div>" +
                "<div class='ticket-location'>" + ticket.hallName + "：" + getSeats(ticket.seats) + "</div>" +
                "<div class='ticket-state'>" + 
                "<span class='ticket-fare'> 总价：" + ticket.fare * ticket.seats.length + "</span>" +
                "<span class='label " + (ticket.status == 1 ? "label-success" : (!ticket.status ? "label-danger" : "label-default")) + "'>" +
        		(ticket.status == 1 ? "已完成" : (!ticket.status ? "未完成" : "已失效")) + "</span>" +
        		"</div>" +
        		"</div>" +
                "</li>" ;
        });
        $('.ticket-on-list').append(ticketDomStr);
    }
    
    function getDate(startTime, endTime) {
    	var date1 = new Date(startTime);
    	var date2 = new Date(endTime);
    	var res = formatDate(date1);
    	res += " " + formatTime(date1);
    	res += " ~ " + formatTime(date2);
    	return res;
	}
    
    function getSeats(list) {
    	var res = "";
    	list.forEach(function (seat) {
    		res += " ";
    		res += seat.rowIndex + "排"
    		res += seat.columnIndex + "座";
    	});
    	return res;
    }

});
