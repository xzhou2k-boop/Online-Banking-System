/**
 * Created by z00382545 on 10/20/16.
 */

(function ($) {
    $.toggleShowPassword = function (options) {
        var settings = $.extend({
            field: "#password",
            control: "#toggle_show_password",
        }, options);

        var control = $(settings.control);
        var field = $(settings.field)

        control.bind('click', function () {
            if (control.is(':checked')) {
                field.attr('type', 'text');
            } else {
                field.attr('type', 'password');
            }
        })
    };

    $.transferDisplay = function () {
        $("#transferFrom").change(function() {
            if ($("#transferFrom").val() == 'Primary') {
                $('#transferTo').val('Savings');
            } else if ($("#transferFrom").val() == 'Savings') {
                $('#transferTo').val('Primary');
            }
        });

        $("#transferTo").change(function() {
            if ($("#transferTo").val() == 'Primary') {
                $('#transferFrom').val('Savings');
            } else if ($("#transferTo").val() == 'Savings') {
                $('#transferFrom').val('Primary');
            }
        });
    };



}(jQuery));

$(document).ready(function() {
    var confirm = function() {
        bootbox.confirm({
            title: "确认预约",
            message: "确定要创建此预约吗?",
            buttons: {
                cancel: {
                    label: '<i class="fa fa-times"></i> 取消'
                },
                confirm: {
                    label: '<i class="fa fa-check"></i> 确认'
                }
            },
            callback: function (result) {
                if (result == true) {
                    $('#appointmentForm').submit();
                } else {
                    console.log("预约已取消.");
                }
            }
        });
    };

    $.toggleShowPassword({
        field: '#password',
        control: "#showPassword"
    });

    $.transferDisplay();

    var now = new Date();
    var pad = function(n) { return ('0' + n).slice(-2); };
    var currentMinute = Math.floor(now.getMinutes() / 10) * 10+10;
    var minDate = now.getFullYear() + '-' + pad(now.getMonth() + 1) + '-' + pad(now.getDate()) 
                + ' ' + pad(now.getHours()) + ':' + pad(currentMinute);

    $(".form_datetime").datetimepicker({
        format: "yyyy-mm-dd hh:ii",
        autoclose: true,
        todayBtn: true,
        startDate: minDate,
        minuteStep: 10,
        todayHighlight:true,
        language: 'zh-CN'
    });

    $('#submitAppointment').click(function () {
        confirm();
    });

    if ($.fn.DataTable) {
        $.extend(true, $.fn.dataTable.defaults, {
            language: {
                "sProcessing": "处理中...",
                "sLengthMenu": "显示 _MENU_ 条记录",
                "sZeroRecords": "没有匹配记录",
                "sInfo": "显示第 _START_ 至 _END_ 条记录，共 _TOTAL_ 条",
                "sInfoEmpty": "显示第 0 至 0 条记录，共 0 条",
                "sInfoFiltered": "(由 _MAX_ 条记录过滤)",
                "sInfoPostFix": "",
                "sSearch": "搜索:",
                "sUrl": "",
                "sEmptyTable": "表中无数据",
                "sLoadingRecords": "载入中...",
                "sInfoThousands": ",",
                "oPaginate": {
                    "sFirst": "首页",
                    "sPrevious": "上页",
                    "sNext": "下页",
                    "sLast": "末页"
                },
                "oAria": {
                    "sSortAscending": ": 以升序排列",
                    "sSortDescending": ": 以降序排列"
                }
            }
        });
    }

});





