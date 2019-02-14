<template>
  <div class="settingsCalContainer" >
    <div class="uiBox settingsContainerData" >
      <h6 class="title  center">{{ $t('settings.label') }}</h6>
      <div class= "uiContentBox" >
        <p>{{ $t('displayed.calendar.label') }}</p>
        <div class="DisplayedCalendarContainer">
          <span v-for="(cal, index) in displayed" v-show="displayed.length > 0" :key="cal" :class="['calendarName ' + cal.color]" :id="cal.id">
            <span v-exo-tooltip.bottom.body="cal.name">{{ cal.name }}></span>
            <i v-exo-tooltip.bottom.body="$t('calendar.remove')" class="uiIconDel" @click="removeCalendar(cal, index)"></i>
          </span>
        </div>
      </div>
      <div class= "uiContentBox " >
        <p>{{ $t('display.additional.calendar.label') }}</p>
        <input v-model="searchKey" :placeholder="$t('search.calendar.label')" @change="search()" type="text" class="PLFcalendarSearchKey"/>
        <ul id="nonDisplayedCalendarContainer" class="calendarItems NonDisplayedCalendar">
          <li v-for="(nonDispCal, index) in nondisplayed" v-show="nondisplayed.length > 0" :key="nonDispCal" :id="nonDispCal.id" class="calendarItem clearfix" @mouseover="isShowAdd = index" @mouseout="isShowAdd = null">
            <a v-exo-tooltip.bottom.body="$t('calendar.add')" v-show="isShowAdd === index" href="javascript:void(0);" class="addButton pull-right" @click="addDisplayCalendar(nonDispCal, index)">
              <i class="uiIconSimplePlusMini uiIconLightGray"></i>
            </a>
            <a :class="[nonDispCal.color + ' colorBox pull-left']" href="javascript:void(0);"></a>
            <a v-exo-tooltip.bottom.body="nonDispCal.name" href="javascript:void(0);" class="calName">{{ nonDispCal.name }}</a>
          </li>
        </ul>
        <div class="settingValidationButton"><button class=" btn" @click="saveSettings">OK</button></div>
      </div>
    </div>
  </div>
</template>

<script>
  import * as calendarServices from '../calendarServices';

  export default {
    name: "CalendarSettings",
    props: {
      displayed: {
        type: Object,
        required: true
      },
      nondisplayed: {
        type: Object,
        required: true
      }
    },
    data() {
      return {
        isShowAdd: null,
        searchKey: ''
      }
    },
    methods: {
      saveSettings() {
        calendarServices.updateSettings(this.nondisplayed.map(cal => cal.id).join()).then(this.$emit('savedCalendar'));
      },
      addDisplayCalendar(item, i) {
        this.displayed.push(item);
        this.nondisplayed.splice(i, 1);
      },
      removeCalendar(item, i) {
        this.nondisplayed.push(item);
        this.displayed.splice(i, 1);
      },
      search() {
        if ('' !== this.searchKey) {
          var result = [];
          for (let cal of this.nondisplayed) {
            if (cal.name.toLowerCase().includes(this.searchKey.toLowerCase())) {
              result.push(cal);
            }
          }
        }
      }
    }
  };
</script>