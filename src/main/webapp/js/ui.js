function checkBox(){
    // checked
    $('input[name=checkbox]').on('click',function(){
        if($(this).is(':checked')){
            $(this).closest('tr').addClass('checked');
        }else{
            $(this).closest('tr').removeClass('checked');
        }
    });
    
    // checkedAll
    $('#check-all').on('click',function(){
        if($(this).is(':checked')){
            $('input[name=checkbox]').prop('checked',true);
            $('.table').find('tr').addClass('checked');
        }else{
            $('input[name=checkbox]').prop('checked',false);
            $('.table').find('tr').removeClass('checked');
        }
    });
}

function toggleBtn(){
    $(".btn--toggle").on("click",function(){
        $(this).toggleClass('is-active').closest('.content__wrap').toggleClass('is-active')
    })
}

function popupOpen(){
    const popup = $("[data-popup]")
    $(popup).on("click",function(){
        const popupData = $(this).data();
        const popupName = popupData.popup
        $("#" + popupName).addClass("is-active");
    })
}

function popupClose(){
    $(".popup-btn__close").on("click",function(){
        $(this).closest(".popup__wrap").removeClass("is-active");
    })
}

function filterBox(){
    $(".btn__filter").on("click",function(){
        if($(this).hasClass("is-active")){
            $(this).removeClass("is-active");
            $(this).siblings(".filter-box").removeClass("is-active");
        }else{
            $(this).addClass("is-active")
            $(this).siblings(".filter-box").addClass("is-active");
        }
    })
}

function gnbDepth(){
    const depthLink = $("[data-link]")
    depthLink.on("click",function(){
        const depthData = $(this).data();
        const depthName = depthData.link
        if($(`[data-depth="${depthName}"]`).hasClass("is-active")){
            $(`[data-depth="${depthName}"]`).removeClass("is-active");
        }else{
            $(`[data-depth="${depthName}"]`).addClass("is-active");
        }
    });
    
    $(document).on("mouseup",function(e){
        const depthItem = $("[data-depth]")
        const depthLink = $("[data-link]")
        if((!$(depthItem).is(e.target) && $(depthItem).has(e.target).length === 0) && (!$(depthLink).is(e.target) && $(depthLink).has(e.target).length === 0)){
            $(depthItem).removeClass("is-active");
            
        }
    })
}


$(function(){
    checkBox();
    toggleBtn();
    popupOpen();
    popupClose();
    filterBox();
    gnbDepth();
});