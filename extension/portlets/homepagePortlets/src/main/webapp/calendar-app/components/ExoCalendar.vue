<template>
  <div class="calendarPortlet ">
    <div class="calendarPortletData uiBox">
      <h6 class="title clearfix">
        <a v-exo-tooltip.bottom.body="$t('prev.day')" href="#" class="actionIcon prevDate pull-left">
          <i class="uiIconMiniArrowLeft uiIconLightGray"></i>
        </a>
        <a v-exo-tooltip.bottom.body="$t('next.day')" href="#" class="actionIcon nextDate pull-right">
          <i class="uiIconMiniArrowRight uiIconLightGray"></i>
        </a>
        <div class="currentDateContainer">
          <center><a href="#">{{ date_act }}</a></center>
        </div>
      </h6>
      <a v-exo-tooltip.bottom.body="$t('settings.label')" v-show="!isInSpace" class="settingsLink actionIcon pull-right" @click="isSettings = true"><i class="uiIconSetting uiIconLightGray"></i> </a>
      <div v-if="isSettings" id="manage" class="tab-pane fade in active">
        <exo-calendar-settings @savedCalendar="eventSavedCalendar"></exo-calendar-settings>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        date_act: 'toto',
        isInSpace: false,
        isSettings: false
      };
    },
    created() {
      this.initCalendar();
    },
    methods: {
      initCalendar() {
        this.isSettings = false;
      },
      // Triggered when `childToParent` event is emitted by the child.
      // onChildClick
      eventSavedCalendar (calendars) {
        console.log("Hello the child event is catched from parent " + calendars);
        this.isSettings = false;
      }
    }
  };
</script>